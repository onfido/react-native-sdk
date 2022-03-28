module.exports = {
  extends: ['@react-native-community'],

  plugins: ['prettier'],

  rules: {
    'prettier/prettier': [
      'error',
      {
        singleQuote: true,
        tabWidth: 2,
        trailingComma: 'all',
        useTabs: false,
        printWidth: 100,
      },
    ],
  },

  globals: {
    __DEV__: true,
  },
};
