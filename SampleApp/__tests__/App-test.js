/**
 * @format
 */

import 'react-native';
import React from 'react';
import App from '../App';

// Note: test renderer must be required after react-native.
import renderer from 'react-test-renderer';

jest.mock('@onfido/react-native-sdk', () => {
  return {
    Onfido: {
      OnfidoSdk: {
        start: jest.fn().mockReturnValue(Promise.resolve()),
      },
    },
  };
});

it('renders correctly', () => {
  renderer.create(<App />);
});
