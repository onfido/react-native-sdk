//
//  OnfidoConfigParser.swift
//
//  Copyright © 2016-2023 Onfido. All rights reserved.
//

import Foundation

struct OnfidoConfigParser {
    func parse(_ config: NSDictionary) throws -> OnfidoPluginConfig {
        try decodeConfig(from: config)
    }
    
    private func decodeConfig<T: Codable>(from dictionary: NSDictionary) throws -> T {
        let jsonData = try JSONSerialization.data(withJSONObject: dictionary, options: [])
        let decoder = JSONDecoder()
        let config = try decoder.decode(T.self, from: jsonData)
        return config
    }
}
