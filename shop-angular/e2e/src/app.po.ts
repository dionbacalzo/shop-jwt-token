import { browser, by, element } from 'protractor';

export class AppPage {
  navigateToContentPage() {
    return browser.get('/content');
  }

  getTitleText() {    
    return element(by.tagName('h1')).getText();    
  }
}
