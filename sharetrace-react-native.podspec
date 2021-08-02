require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "sharetrace-react-native"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  sharetrace-react-native
                   DESC
  s.homepage     = "https://github.com/sharetrace/sharetrace-react-native"
  # brief license entry:
  s.license      = "MIT"
  # optional - use expanded license entry instead:
  # s.license    = { :type => "MIT", :file => "LICENSE" }
  s.authors      = { "Sharetrace" => "sharetrace@shoot.net.cn" }
  s.platforms    = { :ios => "8.0" }
  s.source       = { :git => "https://github.com/sharetrace/sharetrace-react-native.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,c,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency 'SharetraceSDK', "~> 2.3.2"
end

