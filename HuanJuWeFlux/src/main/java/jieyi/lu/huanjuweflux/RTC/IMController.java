package jieyi.lu.huanjuweflux.RTC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/im")
public class IMController {

    @Autowired
    private IMService imService;

    /**
     * 获取私聊历史消息
     */
    @GetMapping("/history/private/{userId}/{friendId}")
    public Flux<MessageDTO> getPrivateHistory(
            @PathVariable Long userId,
            @PathVariable Long friendId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return imService.getHistoryMessages(userId, friendId, 1, page, size);
    }

    /**
     * 获取群聊历史消息
     */
    @GetMapping("/history/group/{groupId}")
    public Flux<MessageDTO> getGroupHistory(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return imService.getHistoryMessages(null, groupId, 2, page, size);
    }

    /**
     * 获取用户在线状态
     */
    @GetMapping("/status/{userId}")
    public Mono<Boolean> getUserStatus(@PathVariable Long userId) {
        return imService.isUserOnline(userId);
    }
}
