package com.search.indexserver.pojo;

import com.google.common.base.Joiner;

import java.util.List;

/**
 * Created by yjj on 15/12/4.
 *
 */
public class TermCodeAndTermInfoList {

    private Integer termCode;
    private List<TermInOneDoc> termInOneDocList;

    public TermCodeAndTermInfoList(Integer termCode, List<TermInOneDoc> termInOneDocList) {
        this.termCode = termCode;
        this.termInOneDocList = termInOneDocList;
    }

    public Integer getTermCode() {
        return termCode;
    }

    public void setTermCode(Integer termCode) {
        this.termCode = termCode;
    }

    public List<TermInOneDoc> getTermInOneDocList() {
        return termInOneDocList;
    }

    public void setTermInOneDocList(List<TermInOneDoc> termInOneDocList) {
        this.termInOneDocList = termInOneDocList;
    }

    @Override
    public String toString() {
        return "TermCodeAndTermInfoList{" +
                "termCode=" + termCode +
                ", termInOneDocList=" + Joiner.on(" ").join(termInOneDocList) +
                '}';
    }
}
