module.exports = {
  preset: 'react-native',
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json', 'node'],
  transformIgnorePatterns: [
    'node_modules/(?!(react-native' +
      '|@onfido/react-native-sdk' +
      '|react-router-native' +
      ')/)',
  ],
  testMatch: ['**/*+(__tests__/**-test.js)'],
};
