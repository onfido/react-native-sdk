/* eslint-env detox/detox, jest */

import {waitForElementByText} from '../TestUtils/waitForElement';
import {waitForElementByType} from '../TestUtils/waitForElement';
import {textProps} from '../TestUtils/testProperties';

describe('Android Sample App flow', () => {
  beforeEach(async function() {
    await device.reloadReactNative();
  });

  it('should complete the document and face flow on Android successfully', async () => {
    await element(by.type('android.widget.EditText'))
      .atIndex(0)
      .typeText(textProps.firstName);
    await element(by.type('android.widget.EditText'))
      .atIndex(1)
      .typeText(textProps.lastName);
    await element(by.text('Start SDK')).tap();
    await element(by.text('LAUNCH')).tap();
    await element(by.text('Start')).tap();
    await element(by.text('National Identity Card')).tap();
    await element(by.text('Bahrain')).tap();
    await element(by.type('androidx.appcompat.widget.AppCompatImageView'))
      .atIndex(0)
      .tap();
    await waitForElementByText('My card is readable');
    await element(by.text('My card is readable')).tap();
    await waitForElementByText('Take a new picture');
    await element(by.text('Take a new picture')).tap();
    await waitForElementByType('androidx.appcompat.widget.AppCompatImageView');
    await element(by.type('androidx.appcompat.widget.AppCompatImageView'))
      .atIndex(0)
      .tap();
    await waitForElementByText('My card is readable');
    await element(by.text('My card is readable')).tap();
    await waitForElementByType('androidx.appcompat.widget.AppCompatImageView');
    await element(by.type('androidx.appcompat.widget.AppCompatImageView'))
      .atIndex(0)
      .tap();
    await waitForElementByText('My card is readable');
    await element(by.text('My card is readable')).tap();
    await waitForElementByText('Continue');
    await element(by.text('Continue')).tap();
    await waitForElementByText('Start recording');
    await element(by.text('Start recording')).tap();
    await waitForElementByText('Next step');
    await element(by.text('Next step')).tap();
    await waitForElementByText('Finish recording');
    await element(by.text('Finish recording')).tap();
    await element(by.text('Submit video')).tap();
  });
});
