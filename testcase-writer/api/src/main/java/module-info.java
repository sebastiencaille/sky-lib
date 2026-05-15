import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.api {
    exports ch.scaille.tcwriter.annotations;
    exports ch.scaille.tcwriter.recorder;
    
    opens ch.scaille.tcwriter.annotations;
    opens ch.scaille.tcwriter.recorder;
    
    requires org.jspecify;
    requires static lombok;
}