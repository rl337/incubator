public class OscADSRInstrument {
   Osc osc;
   ADSR adsr;
   Pan2 bus;

   function void init(Osc o) {
       o @=> osc;
       osc => adsr => bus;
   }

   function void play(float freq, dur duration, float gain) {
      gain => bus.gain;
      freq => osc.freq;

      duration / 10 => dur attack_dur;
      duration * 2 / 10 => dur decay_dur;
      duration * 6 / 10 => dur sustain_dur;
      duration * 1 / 10 => dur release_dur;
      0.8 * gain => float sustain_lvl;

      (attack_dur, decay_dur, sustain_lvl, release_dur) => adsr.set;
      1 => adsr.keyOn;
      attack_dur => now;
      1 => adsr.keyOff;
      duration - attack_dur => now;
   }

}
