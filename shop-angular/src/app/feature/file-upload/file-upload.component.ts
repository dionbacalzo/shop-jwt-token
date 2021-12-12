import { Component, OnInit } from '@angular/core';
import { FileUploadService } from 'src/app/service/file-upload.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MessageService, Message } from 'src/app/service/message.service';


@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {

  fileToUpload: File = null;

  uploadForm: FormGroup;

  constructor(
    private fileUploadService: FileUploadService,
    private messageService: MessageService
  ) { }

  ngOnInit() {
    this.messageService.clear();

    this.uploadForm = new FormGroup({
      file: new FormControl('', Validators.compose(
        [Validators.required, Validators.pattern('^(.)+\.(txt)$')]))
    });
  }

  get file() { return this.uploadForm.get('file'); }

  handleFileInput(event) {
    if (event.target.files.length > 0) {
      this.fileToUpload = event.target.files[0];
    }
  }

  uploadFileToActivity() {
    this.messageService.clear();
    this.fileUploadService.postFile(this.fileToUpload).subscribe(data => {
      data = JSON.parse(data);

      if (data.status === 'SUCCESS') {
        data.status = 'info';
      } else {
        data.status = 'error';
      }

      const message = new Message({
        type: data.status,
        messageDisplay: data.message
      });
      this.messageService.add(message);
      this.uploadForm.reset();
    }, error => {
      this.messageService.handleError(
        error,
        'Unable to upload file'
      );
    });
  }

}
