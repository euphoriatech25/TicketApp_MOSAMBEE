package com.technosales.net.buslocationannouncement.printlib;

import com.pax.dal.ISys;
import com.pax.dal.entity.EBeepMode;
import com.technosales.net.buslocationannouncement.TicketBusApp;

public class SysTester {
    private static SysTester sysTester;
    private ISys iSys = null;
    private SysTester() {
        iSys = TicketBusApp.getDal().getSys();
    }
    public static SysTester getInstance() {
        if (sysTester == null) {
            sysTester = new SysTester();
        }
        return sysTester;
    }
    public void beep(final EBeepMode beepMode, final int delayTime) {
        iSys.beep(beepMode, delayTime);
      }
}
