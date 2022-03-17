const {
	withPlugins,
	ConfigPlugin,
	withAppBuildGradle,
	withProjectBuildGradle,
} = require("@expo/config-plugins");

// podfile platform 10??
// permissions
// weird save thing

// const withOnfidoIOS: ConfigPlugin = (config) => {
// 	return config;
// 	return withPodfileProperties(config, async (config: ExpoConfig) => {

// 	})
// 	// ios config files incl headers
// };

// UH seems like build.gradle already has mavenCentral
// android/build.gradle:
// 		allprojects {
//  	 repositories {
//     		mavenCentral()
//   		}
// 		}
// const withOnfidoAndroidProject = (config) => {
// 	return withProjectBuildGradle(config, async (config) => {
// 		// projectBuildGradle.repositories.push("mavenCentral()")
// 		const content = config.modResults.content;
// 		console.log(content);
// 		if (typeof content !== "string") {
// 			throw new Error("projectBiuldGradle is type" + typeof content);
// 		} else {
// 			console.lo;
// 			if (!content.includes("maven()")) {
// 				config.modResults.content = content.replace(
// 					"repositories {}",
// 					`  repositories {
// 						mavenCentral()
// 					  }
// 					  `
// 				);
// 			} else {
// 				console.log("already got the maven");
// 			}
// 		}
// 		return config;
// 	});
// };

// android/app/build.gradle:
// 	android {
//  	 defaultConfig {
//     	 multiDexEnabled true
//   	}
// 	}
const withOnfidoAndroidApp = (config) => {
	return withAppBuildGradle(config, (config) => {
		const contents = config.modResults.contents;
		const checker = "multiDexEnabled true";
		if (contents.includes(checker)) {
			return contents;
		}

		const oldConfig = "defaultConfig {";
		const newConfig = `defaultConfig {
		multiDexEnabled true`;

		config.modResults.contents = contents.replace(oldConfig, newConfig);

		return config;
	});
};

function withOnfido(config) {
	return withOnfidoAndroidApp(config);
	// return withPlugins(config, [
	// withOnfidoAndroidApp,
	// withOnfidoAndroidProject,
	// withOnfidoIOS
	// ]);
}

module.exports = withOnfido;
