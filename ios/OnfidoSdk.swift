//
//  OnfidoSdk.swift
//
//  Copyright © 2016-2023 Onfido. All rights reserved.
//

import Onfido
import Foundation
import React

// Analytics to be re-added once payloads are harmonised across platforms
private enum CallbackType {
    case media
}

@objc(OnfidoSdk)
final class OnfidoSdk: RCTEventEmitter {

    private let onfidoFlowBuilder = OnfidoFlowBuilder()
    private let configParser = OnfidoConfigParser()
    private var callbackTypes: [CallbackType] = []
    
    @objc
    func start(_ config: NSDictionary,
                     resolver resolve: @escaping RCTPromiseResolveBlock,
                     rejecter reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async { [weak self] in
            self?.run(withConfig: config, resolver: resolve, rejecter: reject)
        }
    }

    private func run(withConfig config: NSDictionary,
                     resolver resolve: @escaping RCTPromiseResolveBlock,
                     rejecter reject: @escaping RCTPromiseRejectBlock) {
        do {
            let onfidoConfig: OnfidoPluginConfig = try configParser.parse(config)

            let appearanceFilePath = Bundle.main.path(forResource: "colors", ofType: "json")
            let appearance = try loadAppearanceFromFile(filePath: appearanceFilePath)

            let mediaCallback: CallbackReceiver?
            if callbackTypes.contains(.media) {
                mediaCallback = CallbackReceiver(withCallback: processMediaResult(_:))
            } else {
                mediaCallback = nil
            }
            
            let onfidoFlow: OnfidoFlow = try onfidoFlowBuilder.build(
                with: onfidoConfig,
                appearance: appearance,
                customMediaCallback: mediaCallback
            )

            onfidoFlow
                .with(responseHandler: { response in
                    switch response {
                    case let .error(error):
                        reject("\(error)", "Encountered an error running the flow", error)
                        return;
                    case let .success(results):
                        resolve(createResponse(results))
                        return;
                    case let .cancel(reason):
                        switch reason {
                        case .deniedConsent:
                            reject("deniedConsent", "User denied consent.", nil)
                        case .userExit:
                            reject("userExit", "User canceled flow.", nil)
                        default:
                            reject("userExit", "User canceled flow via unknown method.", nil)
                        }
                        return;
                    default:
                        reject("error", "Unknown error has occured", nil)
                        return
                    }
                })

            let onfidoViewController = try onfidoFlow.run()
            onfidoViewController.modalPresentationStyle = .fullScreen
            UIApplication.shared.windows.first?.rootViewController?
                .findTopMostController()?
                .present(onfidoViewController, animated: true)
        } catch let error as NSError {
            reject("\(error)", error.domain, error)
            return
        } catch {
            reject("\(error)", "Error running Onfido SDK", error)
            return
        }
    }

    // MARK: - Callbacks

    @objc
    public override func supportedEvents() -> [String] {
        return ["onfidoMediaCallback"]
    }

    @objc
    override static func requiresMainQueueSetup() -> Bool {
        return false
    }

    // MARK: Media

    @objc
    func withMediaCallbacksEnabled() {
        callbackTypes.append(.media)
    }

    private func processMediaResult(_ dictionary: [String: Any]) {
        sendEvent(withName: "onfidoMediaCallback", body: dictionary)
    }
}

public extension UIViewController {
    func findTopMostController() -> UIViewController? {
        var topController: UIViewController? = self
        while topController?.presentedViewController != nil {
            topController = topController?.presentedViewController
        }
        return topController
    }
}

extension Dictionary where Key == String {
    func getColor(withName name: String, fallback: UIColor) -> UIColor {
        if let colorString = self[name] as? String {
            return UIColor.from(hex: colorString)
        } else {
            return fallback
        }
    }
}
