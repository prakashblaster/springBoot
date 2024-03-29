package in.dotworld.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import in.dotworld.exception.FileStorageException;
import in.dotworld.exception.MyFileNotFoundException;
import in.dotworld.model.Compliant;
import in.dotworld.model.Employee;
import in.dotworld.repository.CompliantRepository;

@Service
public class FileStorageService {

	@Autowired
	private CompliantRepository cRepository;

	public Compliant storeFile(MultipartFile file, String id) {

		Compliant compliant = cRepository.findById(id).get();
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		compliant.setAttachment(fileName);

		try {
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}
			compliant.setFileType(file.getContentType());
			compliant.setData(file.getBytes());
			return cRepository.save(compliant);
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	public Compliant getFile(String FildId) {
		return cRepository.findById(FildId)
				.orElseThrow(() -> new MyFileNotFoundException("File not found with id " + FildId));
	}

	public String saveCompliant(Compliant compliant) {
		String type = compliant.getCompliantType();
		compliant.setCompliantType(type.toUpperCase());
		cRepository.save(compliant);

		String compliantById = ServletUriComponentsBuilder.fromCurrentContextPath().path("/compliants")
				.path("/" + compliant.getId()).toUriString();

		String compliantlink = ServletUriComponentsBuilder.fromCurrentContextPath().path("/compliants").toUriString();
		return "compliant created successfully" + "\n" + "\n" + "Added Compliant" + " " + ":" + " " + compliantById
				+ "\n" + "\n" + "All Compliants" + " " + ":" + " " + compliantlink;

	}

	public List<Compliant> getAllCompliant() {
		return cRepository.findAll();

	}

	public String deleteCompliant(String id) {
		Compliant compliant = cRepository.findById(id).get();
		cRepository.delete(compliant);
		String link = ServletUriComponentsBuilder.fromCurrentContextPath().path("/compliants").toUriString();
		return "deleted successfully" + "\n" + "\n" + "View Compliants" + " " + ":" + " " + link;

	}

	public Compliant getCompliantById(String id) {
		Compliant compliant = cRepository.findById(id).get();
		return compliant;
	}

	public String update(Compliant compliant, String id) {
		Compliant compliantModel = cRepository.findById(id).get();
		if (compliantModel != null) {
			if (compliant.getCompliantType() == null) {
				compliantModel.getCompliantType();
			} else {
				compliantModel.setCompliantType(compliant.getCompliantType().toUpperCase());
			}
			if (compliant.getDescription() == null) {
				compliantModel.getDescription();
			} else {
				compliantModel.setDescription(compliant.getDescription());
			}
			cRepository.saveAndFlush(compliantModel);
		} else {
			throw new RuntimeException("compliant not found");
		}
		String link = ServletUriComponentsBuilder.fromCurrentContextPath().path("/compliants")
				.path("/" + compliantModel.getId()).toUriString();
		return "compliant updated successfully" + "\n" + "\n" + "View Updated Compliant" + " " + ":" + " " + link;
	}

	public String getCompliantType(String id) {
		Compliant compliant = cRepository.findById(id).get();
		return compliant.getCompliantType();
	}

	public String getDescription(String id) {
		Compliant compliant = cRepository.findById(id).get();
		return compliant.getDescription();
	}

	public String getAttachment(String id) {
		Compliant compliant = cRepository.findById(id).get();
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/compliants/").path(id+"/")
				.path(compliant.getAttachment()).toUriString();
		return compliant.getAttachment() + "\n" + "\n" + "download link" + " " + ":" + " " + fileDownloadUri;
	}

}