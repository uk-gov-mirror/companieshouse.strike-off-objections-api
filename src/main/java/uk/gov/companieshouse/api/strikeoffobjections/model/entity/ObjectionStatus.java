package uk.gov.companieshouse.api.strikeoffobjections.model.entity;

public enum ObjectionStatus {
    OPEN,
    PROCESSED,
    SUBMITTED,
    INELIGIBLE_COMPANY_STRUCK_OFF,
    INELIGIBLE_NO_DISSOLUTION_ACTION;

    public boolean isEligibilityError() {
        return this == INELIGIBLE_NO_DISSOLUTION_ACTION ||
                this == INELIGIBLE_COMPANY_STRUCK_OFF;
    }
}
