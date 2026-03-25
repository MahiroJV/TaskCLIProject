public class task {

    private int id;
    private String description;
    private String status;
    private String createdAt;
    private String updatedAt;

    public task(int id,String description,String status, String createdAt, String updatedAt ) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ── Getters (read the value) ──────────────────────────────────────────
    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // ── Setters (write the value) ─────────────────────────────────────────

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }



    public String toString() {
        String paddedStatus = String.format("%-11s", status);
        return String.format("[%d] %s (%s) | Created: %s | Updated %s", id, description, paddedStatus, createdAt, updatedAt);
    }

}
