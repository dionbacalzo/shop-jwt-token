export class Util<T> {

	static extractData(res: Response) {
		const body = res;
		return body || {};
	}

	static capitalize = function(string) {
		return string.charAt(0).toUpperCase() + string.slice(1);
	}

}