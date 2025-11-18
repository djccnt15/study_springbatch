package com.djccnt15.study_springbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudySpringbatchApplication {

	public static void main(String[] args) {
		// SpringApplication.run(StudySpringbatchApplication.class, args);
		// Job 실행 결과에 따른 종료 코드(BatchStatus enum의 ordinal)를 Spring 애플리케이션 종료 코드로 출력
        System.exit(SpringApplication.exit(
            SpringApplication.run(StudySpringbatchApplication.class, args)
        ));
	}

}
