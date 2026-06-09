package com.jobprep.studytracker.todo.dto;

import java.io.Serializable;

public record TodoStatsResponse(
        long totalCount,
        long completedCount,
        long activeCount
) implements Serializable {
    public static TodoStatsResponse of(long totalCount, long completedCount) {
        return new TodoStatsResponse(totalCount, completedCount, totalCount - completedCount);
    }
}