import com.google.api.services.bigquery.model.TableRow
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.TypedRead.Method
import org.apache.beam.sdk.options.{PipelineOptions, PipelineOptionsFactory}
import org.apache.beam.sdk.transforms.{InferableFunction, MapElements}

import scala.jdk.CollectionConverters.SeqHasAsJava

object Main {

  private val config = new {
    val pipelineOpts: PipelineOptions =
      PipelineOptionsFactory
      .fromArgs(
        "--runner=directrunner", // or with dataflow runner
        "--project=my_project",
        "--tempLocation=gs://my-bucket/temp",
        "--defaultWorkerLogLevel=INFO"
      )
      .withValidation()
      .create()
  }

  case class MyTableClass(
    a_timestamp_partitioning_field: java.sql.Timestamp,
    b: Int,
    c_boolean_clustering_field: Boolean
  )

  def main(args: Array[String]): Unit = {
    val pipeline = Pipeline.create(config.pipelineOpts)

    BigQueryIO
      .readTableRows()
      .withMethod(Method.DIRECT_READ)
      .from("my_project:my_dataset.my_table")
      .withSelectedFields(List("a_timestamp_partitioning_field", "b", "c_boolean_clustering_field").asJava)
      .withRowRestriction(
        """DATE(a_timestamp_partitioning_field) BETWEEN '2021-01-01' AND '20201-01-31'
          | AND NOT c_boolean_clustering""".stripMargin)
      .apply(
        "TableRowToMyCaseClass",
        MapElements.via(new InferableFunction[TableRow, MyTableClass]() {
          override def apply(input: TableRow): MyTableClass = ??? // f : TableRow -> MyTableClass
        })
      )
      .setCoder(???) // Coder[MyTableClass]

    pipeline.run()
  }
}
