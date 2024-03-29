package in.dotworld.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import in.dotworld.model.Compliant;
import in.dotworld.service.FileStorageService;

@RestController
public class FileUploadController {

	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping("/compliants/{id}/file")
	@ResponseStatus(value = HttpStatus.CREATED)
	public String uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String id) {
		Compliant fileName = fileStorageService.storeFile(file, id);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/compliants/")
				.path( id )
				.path(fileName.getAttachment()).toUriString();
		String fileDownloadUri1 = ServletUriComponentsBuilder.fromCurrentContextPath().path("/compliants/")
				.path( id )
				.toUriString();

		return "uploaded successfully" + "\n" + "download link"+" "+":"+" "+ fileDownloadUri
				+"\n"+"view compliant"+" "+":"+" "+fileDownloadUri1;
	}

}