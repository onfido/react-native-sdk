//
//  EncryptedBiometricTokenHandlerReceiver.swift
//
//  Copyright © 2016-2025 Onfido. All rights reserved.
//

import Foundation
import Onfido

final class EncryptedBiometricTokenHandlerReceiver {
    private let onTokenRequestedCallback: (([String: String]) -> Void)?
    private let onTokenGeneratedCallback: (([String: String]) -> Void)?

    private var onTokenRequestedCompletion: ((String) -> Void)?

    init(
        withTokenRequestedCallback tokenRequestedCallback: (([String: String]) -> Void)?,
        andTokenGeneratedCallback tokenGeneratedCallback: (([String: String]) -> Void)?
    ) {
        self.onTokenRequestedCallback = tokenRequestedCallback
        self.onTokenGeneratedCallback = tokenGeneratedCallback
    }

    func provide(encryptedBiometricToken: String) {
        onTokenRequestedCompletion?(encryptedBiometricToken)
    }
}

extension EncryptedBiometricTokenHandlerReceiver: EncryptedBiometricTokenHandler {
    func onTokenRequested(customerUserHash: String, completion: @escaping (String) -> Void) {
        onTokenRequestedCompletion = completion
        let dictionary = [
            "customerUserHash" : customerUserHash
        ]
        onTokenRequestedCallback?(dictionary)
    }

    func onTokenGenerated(customerUserHash: String, encryptedBiometricToken: String) {
        let dictionary = [
            "customerUserHash" : customerUserHash,
            "encryptedBiometricToken" : encryptedBiometricToken
        ]
        onTokenGeneratedCallback?(dictionary)
    }
}
