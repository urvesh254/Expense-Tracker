package com.ukpatel.expense.tracker.attachment.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AttachmentSessionManagementService {

    public static UUID generateNewSessionId(UUID sessionId) {
        return sessionId != null ? sessionId : UUID.randomUUID();
    }
}
