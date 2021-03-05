package com.example.untact.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.example.untact.dao.GenFileDao;
import com.example.untact.dto.GenFile;
import com.example.untact.dto.ResultData;
import com.example.untact.util.Util;
import com.google.common.base.Joiner;

@Service
public class GenFileService {

	@Value("${custom.genFileDirPath}")
	private String genFileDirPath;

	@Autowired
	private GenFileDao genFileDao;

	public ResultData saveMeta(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo, String originFileName, String fileExtTypeCode, String fileExtType2Code, String fileExt, int fileSize, String fileDir) {
		Map<String, Object> param = Util.mapOf("relTypeCode", relTypeCode, "relId", relId, "typeCode", typeCode, "type2Code", type2Code, "fileNo", fileNo, "originFileName", originFileName, "fileExtTypeCode", fileExtTypeCode, "fileExtType2Code", fileExtType2Code, "fileExt", fileExt, "fileSize",
				fileSize, "fileDir", fileDir);

		genFileDao.saveMeta(param);

		int id = Util.getAsInt(param.get("id"), 0);
		return new ResultData("S-1", "성공하였습니다.", "id", id);
	}

	public ResultData save(MultipartFile multipartFile) {
		String fileInputName = multipartFile.getName();
		String[] fileInputNameBits = fileInputName.split("__");

		if (fileInputNameBits[0].equals("file") == false) {
			return new ResultData("F-1", "파라미터 명이 올바르지 않습니다.");
		}

		int fileSize = (int) multipartFile.getSize();

		if (fileSize <= 0) {
			return new ResultData("F-1", "파일 업로드에 실패 하였습니다.");
		}

		String relTypeCode = fileInputNameBits[1];
		int relId = Integer.parseInt(fileInputNameBits[2]);
		String typeCode = fileInputNameBits[3];
		String type2Code = fileInputNameBits[4];
		int fileNo = Integer.parseInt(fileInputNameBits[5]);
		String originFileName = multipartFile.getOriginalFilename();
		String fileExtTypeCode = Util.getFileExtTypeCodeFromFileName(multipartFile.getOriginalFilename());
		String fileExtType2Code = Util.getFileExtType2CodeFromFileName(multipartFile.getOriginalFilename());
		String fileExt = Util.getFileExtFromFileName(multipartFile.getOriginalFilename()).toLowerCase();
		String fileDir = Util.getNowYearMonthDateStr();

		if (fileExt.equals("jpeg")) {
			fileExt = "jpg";
		} else if (fileExt.equals("htm")) {
			fileExt = "html";
		}

		if (relId > 0) {
			GenFile oldGenFile = getGenFile(relTypeCode, relId, typeCode, type2Code, fileNo);
			System.out.println("삭제까지 되기전 확인");
			if (oldGenFile != null) {
				deleteGenFile(oldGenFile);
				System.out.println("삭제까지 되는지 확인");
			}
		}
		ResultData saveMetaRd = saveMeta(relTypeCode, relId, typeCode, type2Code, fileNo, originFileName, fileExtTypeCode, fileExtType2Code, fileExt, fileSize, fileDir);
		int newGenFileId = (int) saveMetaRd.getBody().get("id");

		// 새 파일이 저장될 폴더(io파일) 객체 생성	
		String targetDirPath = genFileDirPath + "/" + relTypeCode + "/" + fileDir;
		java.io.File targetDir = new java.io.File(targetDirPath);

		// 새 파일이 저장될 폴더가 존재하지 않는다면 생성
		if (targetDir.exists() == false) {
			targetDir.mkdirs();
		}

		String targetFileName = newGenFileId + "." + fileExt;
		String targetFilePath = targetDirPath + "/" + targetFileName;

		// 파일 생성(업로드된 파일을 지정된 경로로 옮김)
		try {
			multipartFile.transferTo(new File(targetFilePath));
		} catch (IllegalStateException | IOException e) {
			return new ResultData("F-3", "파일저장에 실패하였습니다.");
		}
		return new ResultData("S-1", "파일이 생성되었습니다.", "id", newGenFileId, "fileRealPath", targetFilePath, "fileName", targetFileName, "fileInputName", fileInputName);
	}

	public GenFile getGenFile(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo) {
		return genFileDao.getGenFile(relTypeCode, relId, typeCode, type2Code, fileNo);
	}

	public ResultData saveFiles(Map<String, Object> param, MultipartRequest multipartRequest) {		
		// 업로드 시작
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

		Map<String, ResultData> filesResultData = new HashMap<>();
		List<Integer> genFileIds = new ArrayList<>();

		for (String fileInputName : fileMap.keySet()) {
			MultipartFile multipartFile = fileMap.get(fileInputName);

			if (multipartFile.isEmpty() == false) {
				ResultData fileResultData = save(multipartFile);
				int genFileId = (int) fileResultData.getBody().get("id");
				genFileIds.add(genFileId);

				filesResultData.put(fileInputName, fileResultData);
			}
		}

		String genFileIdsStr = Joiner.on(",").join(genFileIds);

		// 삭제 시작
		int deleteCount = 0;

		for (String inputName : param.keySet()) {
			String[] inputNameBits = inputName.split("__");

			if (inputNameBits[0].equals("deleteFile")) {
				String relTypeCode = inputNameBits[1];
				int relId = Integer.parseInt(inputNameBits[2]);
				String typeCode = inputNameBits[3];
				String type2Code = inputNameBits[4];
				int fileNo = Integer.parseInt(inputNameBits[5]);

				GenFile oldGenFile = getGenFile(relTypeCode, relId, typeCode, type2Code, fileNo);

				if (oldGenFile != null) {
					deleteGenFile(oldGenFile);
					deleteCount++;
				}
			}
		}

		return new ResultData("S-1", "파일을 업로드하였습니다.", "filesResultData", filesResultData, "genFileIdsStr", genFileIdsStr, "deleteCount", deleteCount);
	}

	public void changeRelId(int id, int relId) {
		genFileDao.changeRelId(id, relId);
	}

	public void deleteGenFiles(String relTypeCode, int relId) {
		List<GenFile> genFiles = genFileDao.getGenFiles(relTypeCode, relId);

		for (GenFile genFile : genFiles) {
			deleteGenFile(genFile);
		}
	}

	private void deleteGenFile(GenFile genFile) {
		String filePath = genFile.getFilePath(genFileDirPath);
		Util.deleteFile(filePath);

		genFileDao.deleteFile(genFile.getId());
	}

	public List<GenFile> getGenFiles(String relTypeCode, int relId, String typeCode, String type2Code) {
		return genFileDao.getGenFiles(relTypeCode, relId, typeCode, type2Code);
	}

	public GenFile getGenFile(int id) {
	    return genFileDao.getGenFileById(id);
	}
}
