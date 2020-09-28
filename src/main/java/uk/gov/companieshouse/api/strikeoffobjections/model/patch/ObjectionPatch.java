package uk.gov.companieshouse.api.strikeoffobjections.model.patch;

import uk.gov.companieshouse.api.strikeoffobjections.model.entity.ObjectionStatus;

public class ObjectionPatch {
    private String fullName;
    private Boolean shareIdentity;
    private String reason;
    private ObjectionStatus status;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean isShareIdentity() {
        return shareIdentity;
    }

    public void setShareIdentity(Boolean shareIdentity) {
        this.shareIdentity = shareIdentity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ObjectionStatus getStatus() {
        return status;
    }

    public void setStatus(ObjectionStatus status) {
        this.status = status;
    }
}
