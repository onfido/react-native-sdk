require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "onfido-react-native-sdk"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                   onfido-react-native-sdk
                   DESC
  s.homepage     = "https://github.com/onfido/react-native-sdk"
  s.license      = "MIT"
  s.authors      = { "Onfido" => "engineering@onfido.com" }
  s.platforms    = { :ios => "11.0" }
  s.source       = { :git => "https://github.com/onfido/react-native-sdk.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,swift}"
  s.exclude_files = "ios/OnfidoSdkTests/"
  s.requires_arc = true

  s.dependency "React"
  s.dependency "Onfido", "~> 29.2.1"
end
