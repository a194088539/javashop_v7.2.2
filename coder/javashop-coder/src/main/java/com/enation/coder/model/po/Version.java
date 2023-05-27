package com.enation.coder.model.po;

/**
 * 字段实体
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2019年3月9日 下午2:09:50
 */
public class Version {

    private static final long serialVersionUID = -4296815557987838189L;

    private Integer id;

    private String version;

    private long add_time;

    private Integer project_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }
}
