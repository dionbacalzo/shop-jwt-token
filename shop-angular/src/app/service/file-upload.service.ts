import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Constant } from '../object/constant';

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {

  constructor(private http: HttpClient) { }

  postFile(fileToUpload: File): Observable<any> {
    const formData = new FormData();
		formData.append("file", fileToUpload);        
    return this.http
      .post(Constant.endpoint + 'uploadItems', formData, { responseType: 'text' })      
  }

}
