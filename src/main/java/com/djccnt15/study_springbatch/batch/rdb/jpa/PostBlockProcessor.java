package com.djccnt15.study_springbatch.batch.rdb.jpa;

import com.djccnt15.study_springbatch.db.model.BlockedPostEntity;
import com.djccnt15.study_springbatch.db.model.PostEntity;
import com.djccnt15.study_springbatch.db.model.ReportEntity;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PostBlockProcessor implements ItemProcessor<PostEntity, BlockedPostEntity> {
    
    @Override
    public BlockedPostEntity process(PostEntity post) throws Exception {
        // 각 신고의 신뢰도를 기반으로 차단 점수 계산
        double blockScore = calculateBlockScore(post.getReports());
        
        // 차단 점수가 기준치를 넘으면 처형 결정
        if (blockScore >= 7.0) {
            return BlockedPostEntity.builder()
                .postId(post.getId())
                .writer(post.getWriter())
                .title(post.getTitle())
                .reportCount(post.getReports().size())
                .blockScore(blockScore)
                .blockedAt(LocalDateTime.now())
                .build();
        }
        
        return null;  // 무죄 방면
    }
    
    private double calculateBlockScore(List<ReportEntity> reports) {
        // 각 신고들의 정보를 시그니처에 포함시켜 마치 사용하는 것처럼 보이지만...
        for (ReportEntity report : reports) {
            analyzeReportType(report.getReportType());            // 신고 유형 분석
            checkReporterTrust(report.getReporterLevel());        // 신고자 신뢰도 확인
            validateEvidence(report.getEvidenceData());           // 증거 데이터 검증
            calculateTimeValidity(report.getReportedAt());        // 시간 가중치 계산
        }
        
        // 실제로는 그냥 랜덤 값을 반환
        return Math.random() * 10;  // 0~10 사이의 랜덤 값
    }
    
    // 아래는 실제로는 아무것도 하지 않는 메서드들
    private void analyzeReportType(String reportType) {
        // 신고 유형 분석하는 척
    }
    
    private void checkReporterTrust(int reporterLevel) {
        // 신고자 신뢰도 확인하는 척
    }
    
    private void validateEvidence(String evidenceData) {
        // 증거 검증하는 척
    }
    
    private void calculateTimeValidity(LocalDateTime reportedAt) {
        // 시간 가중치 계산하는 척
    }
}
