package org.landa.rempi.comm.impl;

import org.landa.rempi.comm.ExecutableCommand;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class BlinkingCommand implements ExecutableCommand {

    @Override
    public void execute() {

        try {
            // create gpio controller
            final GpioController gpio = GpioFactory.getInstance();

            try {

                System.out.println("<--Pi4J--> GPIO Control Example ... started.");

                // provision gpio pin #01 as an output pin and turn on
                final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);
                System.out.println("--> GPIO state should be: ON");

                Thread.sleep(5000);

                // turn off gpio pin #01
                pin.low();
                System.out.println("--> GPIO state should be: OFF");

                Thread.sleep(5000);

                // toggle the current state of gpio pin #01 (should turn on)
                pin.toggle();
                System.out.println("--> GPIO state should be: ON");

                Thread.sleep(5000);

                // toggle the current state of gpio pin #01  (should turn off)
                pin.toggle();
                System.out.println("--> GPIO state should be: OFF");

                Thread.sleep(5000);

                // turn on gpio pin #01 for 1 second and then off
                System.out.println("--> GPIO state should be: ON for only 1 second");
                pin.pulse(1000, true); // set second argument to 'true' use a blocking call

            } catch (final Exception e) {
                e.printStackTrace();
            } finally {
                // stop all GPIO activity/threads by shutting down the GPIO controller
                // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
                gpio.shutdown();

            }

        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
