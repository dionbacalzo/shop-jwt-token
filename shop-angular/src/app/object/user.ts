export class User<T> {
	username: string;
	firstname: string;	
	lastname: string;
	role: string;
	picture: string;
	token: string;
	
	constructor(options: {
			username?: string,
			firstname?: string,
			lastname?: string,
			role?: string,
			picture?: string,
			token?: string
    	} = {}) {
	    this.username = options.username;
	    this.firstname = options.firstname;
	    this.lastname = options.lastname;
		this.role = options.role;
		this.picture = options.picture;
		this.token = options.token;
	}
}
