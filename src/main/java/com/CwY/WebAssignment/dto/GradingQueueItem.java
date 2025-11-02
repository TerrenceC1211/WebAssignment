package com.CwY.WebAssignment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class GradingQueueItem {
    private final Long assignmentId;
    private final String assignmentTitle;
    private final long totalSubmissions;
    private final long pendingCount;
}
