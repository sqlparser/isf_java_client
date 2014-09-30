
package demos.formatByISFP;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

public class SqlFormat
{

	public static void main( String[] args )
	{
		String sql = "SELECT 1+1 FROM ABC WHERE  output = \"Hello world!\" ";
        String fmtOptions = "{}";
		if ( args.length > 0 )
		{
			File file = new File( args[0] );
			if ( !file.exists( ) )
			{
				System.out.println( "File not exists:" + args[0] );
				return;
			}
			else if ( file.isDirectory( ) )
			{
				System.out.println( "File is a folder:" + args[0] );
				return;
			}
			else
			{
				try
				{
                    if (args[0].endsWith(".json"))
					  fmtOptions = getFileContent( file );
                    else
                      sql = getFileContent( file );
				}
				catch ( Exception e )
				{
					System.out.println( "Read file failed:" + args[0] );
					return;
				}
			}

            if ( args.length > 1 ){

            file = new File( args[1] );
            if ( !file.exists( ) )
            {
                System.out.println( "File not exists:" + args[1] );
                return;
            }
            else if ( file.isDirectory( ) )
            {
                System.out.println( "File is a folder:" + args[1] );
                return;
            }
            else
            {
                try
                {
                    if (args[1].endsWith(".json"))
					  fmtOptions = getFileContent( file );
                    else
                      sql = getFileContent( file );
                }
                catch ( Exception e )
                {
                    System.out.println( "Read file failed:" + args[1] );
                    return;
                }
            }
            }
		}

		try
		{
            //System.out.println(fmtOptions);

			String formatSql = sendPost( "http://www.gudusoft.com/format.php",
					"rqst_db_vendor=oracle&rqst_isf_client=java_client&rqst_input_sql="
							+ URLEncoder.encode( sql, "utf-8" )
                            +"&rqst_formatOptions="+ URLEncoder.encode( fmtOptions, "utf-8" )
                            //+"&rqst_formatOptions=\"\""
                        );
			System.out.println( "json: \n" + formatSql + "\n" );
			System.out.println( "format sql:" );
			JSONObject jsonObject = new JSONObject( formatSql );
			System.out.println( jsonObject.getString( "rspn_formatted_sql" ) );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
	}

	private static String getFileContent( File file ) throws Exception
	{
		RandomAccessFile f = null;
		try
		{
			f = new RandomAccessFile( file, "r" );
			byte[] contents = new byte[(int) f.length( )];
			f.readFully( contents );
			return new String( contents );
		}
		finally
		{
			try
			{
				f.close( );
			}
			catch ( IOException ignore )
			{
			}
		}
	}

	public static String sendPost( String url, String param )
	{
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try
		{
			URL realUrl = new URL( url );
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection( );
			conn.setRequestProperty( "accept", "*/*" );
			conn.setRequestProperty( "connection", "Keep-Alive" );
			conn.setRequestProperty( "Content-Type",
					"application/x-www-form-urlencoded" );
			conn.setRequestProperty( "user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)" );
			conn.setDoOutput( true );
			conn.setDoInput( true );
			conn.connect( );
			out = new PrintWriter( conn.getOutputStream( ) );
			out.print( param );
			out.flush( );
			in = new BufferedReader( new InputStreamReader( conn.getInputStream( ) ) );
			String line;
			while ( ( line = in.readLine( ) ) != null )
			{
				result += line;
			}
		}
		catch ( Exception e )
		{
			System.out.println( "Send post request failed!\n" + e );
			e.printStackTrace( );
		}
		finally
		{
			try
			{
				if ( out != null )
				{
					out.close( );
				}
				if ( in != null )
				{
					in.close( );
				}
			}
			catch ( IOException ex )
			{
				ex.printStackTrace( );
			}
		}
		return result;
	}
}
