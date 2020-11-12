package org.confeti.dataobjects;

public class Status {
    private Stat status;

    public static Status ok() {
        return new Status(Stat.OK);
    }

    public static Status fail() {
        return new Status(Stat.FAIL);
    }

    public Status(Stat status) {
        this.status = status;
    }

    public Stat getStatus() {
        return status;
    }

    public void setStatus(Stat status) {
        this.status = status;
    }

    public enum Stat {
        OK,
        FAIL
    }
}
