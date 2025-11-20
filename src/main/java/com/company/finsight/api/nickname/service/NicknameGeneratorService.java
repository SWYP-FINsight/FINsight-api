package com.company.finsight.api.nickname.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.company.finsight.api.nickname.entity.NicknamePrefix;
import com.company.finsight.api.nickname.entity.NicknameSuffix;

@Service
public class NicknameGeneratorService {

	public String generate() {
		String prefix = NicknamePrefix.random();
		String suffix = NicknameSuffix.random();
		return prefix + suffix + generateSixDigits();
	}

	private String generateSixDigits() {
		Random random = new Random();
		return String.format("%06d", random.nextInt(1_000_000));
	}
}
