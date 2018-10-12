import org.apache.activemq.ActiveMQConnectionFactory
import javax.jms.Session

fun main(args: Array<String>) {
    val cf = ActiveMQConnectionFactory("vm://localhost?broker.persistent=true")

    var conn = cf.createConnection();
    try {
        conn.start();
        val session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        try {
            val queue = session.createQueue("dynamicQueues/test")
            val producer = session.createProducer(queue);
            for (i in 1..200) {
                val message = session.createTextMessage("Message A #$i");
                message.setStringProperty("type", "A");
                producer.send(message)
            }
            for (i in 1..200) {
                val message = session.createTextMessage("Message B #$i");
                message.setStringProperty("type", "B");
                producer.send(message)
            }
        } finally {
            session.close();
        }
    } finally {
        conn.close()
    }

    conn = cf.createConnection();
    try {
        val session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        try {
            val queue = session.createQueue("dynamicQueues/test")
            val consumer = session.createConsumer(queue, "type='B'");
            consumer.setMessageListener { m -> println(m) }
            conn.start();
            Thread.sleep(5000)
        } finally {
            session.close();
        }
    } finally {
        conn.close()
    }
}