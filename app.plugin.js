const {
	withAppBuildGradle,
} = require("@expo/config-plugins");

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
}

module.exports = withOnfido;
