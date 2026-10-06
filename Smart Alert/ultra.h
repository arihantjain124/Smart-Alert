const int trigPin = 9;
const int echoPin = 10;
const unsigned long echoTimeoutMicros = 30000UL;

int reader() {
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  const unsigned long duration = pulseIn(echoPin, HIGH, echoTimeoutMicros);
  if (duration == 0) {
    return -1;
  }

  return static_cast<int>(duration * 0.034F / 2.0F);
}
