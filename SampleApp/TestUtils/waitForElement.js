import {element, by, waitFor, expect} from 'detox';

export const waitForElementByType = async type => {
  await waitFor(element(by.type(`${type}`)).atIndex(0))
    .toBeVisible()
    .withTimeout(9000);
  return expect(element(by.type(`${type}`)).atIndex(0)).toBeVisible();
};

export const waitForElementByLabel = async label => {
  await waitFor(element(by.label(`${label}`)))
    .toBeVisible()
    .withTimeout(9000);
  return expect(element(by.label(`${label}`))).toBeVisible();
};

export const waitForElementByText = async text => {
  await waitFor(element(by.text(`${text}`)))
    .toBeVisible()
    .withTimeout(9000);
  return expect(element(by.text(`${text}`))).toBeVisible();
};
