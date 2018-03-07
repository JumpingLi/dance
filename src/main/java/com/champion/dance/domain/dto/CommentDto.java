package com.champion.dance.domain.dto;

import com.champion.dance.domain.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/28 18:27
 * Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommentDto extends Comment {
    private String realName;
    private String nickname;
    private String avatarUrl;
}
