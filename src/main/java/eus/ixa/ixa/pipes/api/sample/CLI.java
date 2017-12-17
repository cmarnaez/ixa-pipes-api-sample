/*
 *  Copyright 2017 Rodrigo Agerri

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package eus.ixa.ixa.pipes.api.sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.jdom2.JDOMException;

import com.google.common.io.Files;

import eus.ixa.ixa.pipe.nerc.Annotate;
import eus.ixa.ixa.pipe.nerc.train.Flags;
import ixa.kaflib.KAFDocument;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

/**
 * Main class of ixa-pipes-api-sample which uses IXA pipes API.
 * 
 * @author ragerri
 * @version 2017-12-15
 */
public class CLI {

  /**
   * Get dynamically the version of ixa-pipes-api-sample by looking at the
   * MANIFEST file.
   */
  private final String version = CLI.class.getPackage()
      .getImplementationVersion();
  /**
   * Get the git commit of the ixa-pipes-api-sample compiled by looking at the
   * MANIFEST file.
   */
  private final String commit = CLI.class.getPackage()
      .getSpecificationVersion();
  /**
   * Name space of the arguments provided at the CLI.
   */
  private Namespace parsedArguments = null;
  /**
   * Argument parser instance.
   */
  private ArgumentParser argParser = ArgumentParsers
      .newArgumentParser("ixa-pipes-api-sample-" + version + "-exec.jar")
      .description("ixa-pipes-api-sample-" + version
          + " shows how to programatically use IXA pipes.\n");
  /**
   * Sub parser instance.
   */
  private Subparsers subParsers = argParser.addSubparsers()
      .help("sub-command help");
  /**
   * The parser that manages the tokenizer sub-command.
   */
  private Subparser tokenParser;
  private Subparser posParser;
  private Subparser nerParser;
  private Subparser chunkParser;
  private Subparser parseParser;
  private Subparser docParser;
  private Subparser pipelineParser;

  private static final String TOKEN_PARSER = "tok";
  private static final String POS_PARSER = "pos";
  private static final String NER_PARSER = "ner";
  private static final String CHUNK_PARSER = "chunk";
  public static final String PARSER_PARSER = "parse";
  public static final String DOC_PARSER = "doc";

  /**
   * Construct a CLI object with the sub-parsers to manage the command line
   * parameters.
   */
  public CLI() {
    tokenParser = subParsers.addParser(TOKEN_PARSER).help("The tokenizer CLI");
    posParser = subParsers.addParser(POS_PARSER).help("The POS tagger CLI");
    nerParser = subParsers.addParser(NER_PARSER).help("The NER CLI");
    loadNERParameters();
    chunkParser = subParsers.addParser(CHUNK_PARSER).help("The chunker CLI");
    parseParser = subParsers.addParser(PARSER_PARSER)
        .help("The constituent parser CLI");
    docParser = subParsers.addParser(DOC_PARSER)
        .help("The document classifier CLI");
  }

  /**
   * Main entry point of ixa-pipes-api-sample.
   * 
   * @param args
   *          the arguments passed through the CLI
   * @throws IOException
   *           exception if input data not available
   * @throws JDOMException
   *           if problems with the xml formatting of NAF
   */
  public static void main(final String[] args)
      throws IOException, JDOMException {
    CLI cmdLine = new CLI();
    cmdLine.parseCLI(args);
  }

  /**
   * Parse the command interface parameters with the argParser.
   * 
   * @param args
   *          the arguments passed through the CLI
   * @throws IOException
   *           exception if problems with the incoming data
   * @throws JDOMException
   *           if xml format problems
   */
  public final void parseCLI(final String[] args)
      throws IOException, JDOMException {
    try {
      parsedArguments = argParser.parseArgs(args);
      System.err.println("CLI options: " + parsedArguments);
      switch (args[0]) {
      case TOKEN_PARSER:
        tokenize();
        break;
      case POS_PARSER:
        posTag();
        break;
      case NER_PARSER:
        nerTag();
        break;
      case CHUNK_PARSER:
        chunking();
        break;
      case PARSER_PARSER:
        parsing();
        break;
      case DOC_PARSER:
        docClassify();
        break;
      }
    } catch (ArgumentParserException e) {
      argParser.handleError(e);
      System.out.println("Run java -jar ixa-pipes-api-sample-" + version
          + "-exec.jar (tok|pos|ner|chunk|parse|doc) -help for details");
      System.exit(1);
    }
  }

  public final void tokenize() throws IOException, JDOMException {
    BufferedReader breader = new BufferedReader(new InputStreamReader(
        System.in, "UTF-8"));
    BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(
        System.out, "UTF-8"));
    // read KAF document from inputstream
    KAFDocument kaf = KAFDocument.createFromStream(breader);
    //API begin
    
  }

  public final void posTag() {

  }

  public final void nerTag() throws IOException, JDOMException {

    BufferedReader breader = new BufferedReader(
        new InputStreamReader(System.in, "UTF-8"));
    BufferedWriter bwriter = new BufferedWriter(
        new OutputStreamWriter(System.out, "UTF-8"));
    // read KAF document from inputstream
    KAFDocument kaf = KAFDocument.createFromStream(breader);
    // API begin
    // load parameters into a properties
    String model = parsedArguments.getString("model");
    String outputFormat = parsedArguments.getString("outputFormat");
    // building the properties object
    Properties properties = setNERProperties(model);
    KAFDocument.LinguisticProcessor newLp = kaf.addLinguisticProcessor(
        "entities", "ixa-pipe-nerc-" + Files.getNameWithoutExtension(model),
        version + "-" + commit);
    newLp.setBeginTimestamp();
    Annotate annotator = new Annotate(properties);
    annotator.annotateNEs(kaf);
    newLp.setEndTimestamp();
    String kafToString = null;
    // end of API
    if (outputFormat.equalsIgnoreCase("conll03")) {
      kafToString = annotator.annotateNEsToCoNLL2003(kaf);
    } else if (outputFormat.equalsIgnoreCase("conll02")) {
      kafToString = annotator.annotateNEsToCoNLL2002(kaf);
    } else {
      kafToString = kaf.toString();
    }
    bwriter.write(kafToString);
    bwriter.close();
    breader.close();
  }

  public final void chunking() {

  }

  public final void parsing() {

  }

  public final void docClassify() {
  }

  /**
   * Create the available parameters for NER tagging.
   */
  private void loadNERParameters() {

    nerParser.addArgument("-m", "--model").required(true)
        .help("Pass the model to do the tagging as a parameter.\n");
    nerParser.addArgument("-o", "--outputFormat").required(false)
        .choices("conll03", "conll02", "naf")
        .setDefault(Flags.DEFAULT_OUTPUT_FORMAT)
        .help("Choose output format; it defaults to NAF.\n");
  }

  /**
   * Set a Properties object with the CLI parameters for NER annotation.
   * 
   * @param model
   *          the model parameter
   * @param language
   *          language parameter
   * @param lexer
   *          rule based parameter
   * @param dictTag
   *          directly tag from a dictionary
   * @param dictPath
   *          directory to the dictionaries
   * @return the properties object
   */
  private Properties setNERProperties(String model) {
    Properties annotateProperties = new Properties();
    annotateProperties.setProperty("model", model);
    annotateProperties.setProperty("language", "en");
    annotateProperties.setProperty("ruleBasedOption", Flags.DEFAULT_LEXER);
    annotateProperties.setProperty("dictTag", Flags.DEFAULT_DICT_OPTION);
    annotateProperties.setProperty("dictPath", Flags.DEFAULT_DICT_PATH);
    annotateProperties.setProperty("clearFeatures", Flags.DEFAULT_FEATURE_FLAG);
    return annotateProperties;
  }

}
