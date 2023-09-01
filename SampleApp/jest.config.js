module.exports = {
  preset: 'react-native',
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json', 'node'],
  transformIgnorePatterns: [
    'node_modules/(?!(' +
      '@react-native|react-native' +
      '|@onfido/react-native-sdk' +
      '|react-router-native' +
      '|invariant' +
      ')/)',
  ],
  testMatch: ['**/*+(__tests__/**-test.js)'],
  testEnvironment: 'node',
  setupFiles: ['<rootDir>/jest.setup.js'],
};
