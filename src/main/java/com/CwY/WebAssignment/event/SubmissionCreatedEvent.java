package com.CwY.WebAssignment.event;

import com.CwY.WebAssignment.model.Submission;

public record SubmissionCreatedEvent(Submission submission) {
}