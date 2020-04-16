/* eslint-env detox/detox, jest */

import {waitForElementByLabel} from '../TestUtils/waitForElement';
import {textProps} from '../TestUtils/testProperties';

describe('iOS Sample App flow', () => {
  beforeEach(async function() {
    await device.reloadReactNative();
  });

  it('should complete the document and face flow on iOS successfully', async () => {
    await element(by.type('RCTSinglelineTextInputView'))
      .atIndex(1)
      .typeText(textProps.firstName);
    await element(by.type('RCTSinglelineTextInputView'))
      .atIndex(0)
      .typeText(textProps.lastName);
    await element(by.text('Start SDK')).tap();
    await element(by.text('Launch')).tap();
    await element(by.text('Choose document')).tap();
    await element(by.text('Passport')).tap();
    await element(by.label('shutter button')).tap();
    await element(by.text('Take a new picture')).tap();
    await element(by.label('shutter button')).tap();
    await element(by.text('My passport is readable')).tap();
    await element(by.text('Take a new picture'))
      .atIndex(1)
      .tap();
    await element(by.label('shutter button')).tap();
    await element(by.text('My passport is readable')).tap();
    await element(by.text('Continue')).tap();
    await waitForElementByLabel('Start recording');
    await element(by.label('Start recording')).tap();
    await waitForElementByLabel('Next');
    await element(by.label('Next')).tap();
    await waitForElementByLabel('Finish recording');
    await element(by.label('Finish recording')).tap();
    await element(by.text('Submit video')).tap();
  });
});
