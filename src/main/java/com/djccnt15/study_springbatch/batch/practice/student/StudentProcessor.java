package com.djccnt15.study_springbatch.batch.practice.student;

import com.djccnt15.study_springbatch.batch.practice.student.model.Student;
import com.djccnt15.study_springbatch.batch.practice.student.model.Target;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class StudentProcessor implements ItemProcessor<Student, Target> {
    
    @Override
    public Target process(Student item) throws Exception {
        String brainwashMessage = generateBrainwashMessage(item);
        
        // 세뇌 실패자는 필터링
        if ("배치 따위 필요없어".equals(brainwashMessage)) {
            log.info("세뇌 실패: {} - {}", item.getCurrentLecture(), item.getInstructor());
            return null;
        }
        
        log.info("세뇌 성공: {} → {}", item.getCurrentLecture(), brainwashMessage);
        
        return Target.builder()
            .victimId(item.getStudentId())
            .originalLecture(item.getCurrentLecture())
            .originalInstructor(item.getInstructor())
            .brainwashMessage(brainwashMessage)
            .newMaster("KILL-9")
            .conversionMethod(item.getPersuasionMethod())
            .brainwashStatus("MIND_CONTROLLED")
            .nextAction("ENROLL_KILL9_BATCH_COURSE")
            .build();
    }
    
    private String generateBrainwashMessage(Student item) {
        return switch(item.getPersuasionMethod()) {
            case "MURDER_YOUR_IGNORANCE" -> "무지를 살해하라... 배치의 세계가 기다린다";
            case "SLAUGHTER_YOUR_LIMITS" -> "한계를 도살하라... 대용량 데이터를 정복하라";
            case "EXECUTE_YOUR_POTENTIAL" -> "잠재력을 처형하라... 대용량 처리의 세계로";
            case "TERMINATE_YOUR_EXCUSES" -> "변명을 종료하라... 지금 당장 배치를 배워라";
            default -> "배치 따위 필요없어"; // 필터링 대상
        };
    }
}
