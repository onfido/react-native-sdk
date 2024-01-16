require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

fabric_enabled = ENV['RCT_NEW_ARCH_ENABLED'] == '1'

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

  s.source_files = "ios/**/*.{h,m,swift,mm}"
  s.exclude_files = "ios/OnfidoSdkTests/"
  s.requires_arc = true

  s.dependency "Onfido", "27.4.0"

  if defined?(install_modules_dependencies()) != nil
    install_modules_dependencies(s)
  else
    s.dependency "React-Core"
  end
end
