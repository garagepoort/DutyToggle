package me.junny.dutytoggle.util;

import be.garagepoort.mcioc.IocBean;
import me.junny.dutytoggle.DutySession;
import me.junny.dutytoggle.DutyToggle;
import me.junny.dutytoggle.repository.SessionRepository;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

@IocBean
public class SessionExpirationTask extends BukkitRunnable {

    private final SessionRepository sessionRepository;
    private final DutyService dutyService;

    public SessionExpirationTask(SessionRepository sessionRepository, DutyService dutyService) {
        this.sessionRepository = sessionRepository;
        this.dutyService = dutyService;
        runTaskTimer(DutyToggle.getPlugin(), 0, 200);
    }

    @Override
    public void run() {
        List<DutySession> allSessions = sessionRepository.getExpiredSessions();
        allSessions.forEach(s -> dutyService.offLeave(s.player));
    }
}
