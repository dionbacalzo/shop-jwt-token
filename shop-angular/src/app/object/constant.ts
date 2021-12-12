export class Constant<T> {
    public static endpoint = 'http://localhost:8080/shop/';

    public static pagetitle = {
        home : 'Shop Display: Home',
        updateProfile : 'Shop Display: Profile',
        signup: 'Shop Display: Signup',
        admin: 'Shop Display: Admin',
        updateShop: 'Shop Display: Update',
        notFound: 'Page not found'
    }

    public static carouselSrc = [
        { image: { 'background': 'url(./assets/images/background-laptop.jpeg) no-repeat', 'background-size': 'cover' }, text: 'Laptops', textClass: 'mask rgba-black-light' },
        { image: { 'background': 'url(./assets/images/background-phone.jpeg) no-repeat', 'background-size': 'cover' }, text: 'Mobile Phones', textClass: 'mask rgba-black-light' },
        { image: { 'background': 'url(./assets/images/background-accessory.jpeg) no-repeat', 'background-size': 'cover' }, text: 'Earphones', textClass: 'mask rgba-black-light' },
    ];
}