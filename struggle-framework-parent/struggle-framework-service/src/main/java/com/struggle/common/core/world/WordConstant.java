package com.struggle.common.core.world;

import lombok.Getter;

/**
 * Word常量类
 *
 * @Auther: dejun.mei
 * @Date: 2019-10-18
 * @version: 1.0
 */
public class WordConstant {
    /**
     * WORD 相关常量表
     */
    public enum WORD {
        COLLEGE_WORD_PATH("/template"),
        COLLEGE_WORD_FINAL_POSION(".docx");

        @Getter
        private String code;

        WORD(String code) {
            this.code = code;
        }
    }

}
