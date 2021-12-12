import { AppPage } from './app.po';

describe('workspace-project App', () => {
  let page: AppPage;

  beforeEach(() => {
    page = new AppPage();
  });

  it('Should display title', () => {
    page.navigateToContentPage();
    expect(page.getTitleText()).toEqual('SHOP');
  });
});
