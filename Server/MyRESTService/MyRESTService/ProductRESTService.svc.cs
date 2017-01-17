using System;
using System.Collections.Generic;
using System.ServiceModel.Web;
using System.Net;
using System.Collections;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;
using MySql.Data.MySqlClient;
using System.Device.Location;


namespace ToDoList
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the class name "ProductRESTService" in code, svc and config file together.
    // NOTE: In order to launch WCF Test Client for testing this service, please select ProductRESTService.svc or ProductRESTService.svc.cs at the Solution Explorer and start debugging.
    public class ProductRESTService : IToDoService
    {
        //public const string connectionString = "Server=maria.eng.utah.edu;Port=3306;Database=tuber;UID=tobin;Password=traflip53";
        //public const string connectionString = "Server=sql3.freemysqlhosting.net;Database=sql3153117;UID=sql3153117;Password=vjbaNtDruW;";

        public const string connectionString = "Server=23.99.55.197;Database=tuber;UID=tobin;Password=Redpack!99!!";

       // public const string connectionString = "Server=us-cdbr-azure-west-b.cleardb.com;Database=tuber;UID=b7b701147be147;Password=9d871255";
        public List<Product> GetProductList()
        {
            return Products.Instance.ProductList;
        }

        public MakeUserItem MakeUser(UserItem item)
        {
            lock (this)
            {
                String userEmail = item.userEmail;
                String userPassword = item.userPassword;

                using (MySqlConnection conn = new MySqlConnection(connectionString))
                {
                    try
                    {
                        conn.Open();

                        MySqlCommand command = conn.CreateCommand();
                        command.CommandText = "insert into Users2 (userEmail, userPassword) values (?userEmail, ?userPassword)";
                        command.Parameters.AddWithValue("userEmail", userEmail);
                        command.Parameters.AddWithValue("userPassword", userPassword);

                        if (command.ExecuteNonQuery() > 0)
                        {
                            MakeUserItem user = new MakeUserItem();
                            user.userEmail = userEmail;
                            user.userPassword = userPassword;

                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;

                            return user;
                        }
                        else
                        {
                            return new MakeUserItem();
                        }
                    }
                    catch (Exception e)
                    {
                        throw e;
                    }
                }
            }
        }

        /// <summary>
        /// Verify the user provided the correct credentials to login.
        /// 
        /// If correct, respond with response code 200 (OK) and return an UserItem with the fields populated with the user's information.
        /// 
        /// If incorrect, respond with response code 401 (Unauthorized) and return an UserItem with the fields set to null.
        /// </summary>
        /// <param name="data"></param>
        /// <returns></returns>
        public VerifiedUserItem VerifyUser(UserItem data)
        {
            lock (this)
            {
                String userEmail = data.userEmail;
                String userPassword = data.userPassword;

                String returnedUserEmail = "";
                String returnedUserPassword = "";

                using (MySqlConnection conn = new MySqlConnection(connectionString))
                {
                    try
                    {
                        conn.Open();

                        MySqlCommand command = conn.CreateCommand();
                        command.CommandText = "select * from Users2 where userEmail = ?userEmail";
                        command.Parameters.AddWithValue("userEmail", userEmail);

                        using (MySqlDataReader reader = command.ExecuteReader())
                        {
                            while (reader.Read())
                            {
                                returnedUserEmail = reader.GetString("userEmail");
                                returnedUserPassword = reader.GetString("userPassword");
                            }
                        }

                        if (returnedUserEmail != userEmail || userPassword != returnedUserPassword)
                        {
                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                            return new VerifiedUserItem();
                        }
                        else
                        {
                            ArrayList studentCourses = new ArrayList();
                            command.CommandText = "select name from student_courses where email = ?email";
                            command.Parameters.AddWithValue("email", returnedUserEmail);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    studentCourses.Add(reader.GetString("name"));
                                }
                            }

                            ArrayList tutorCourses = new ArrayList();
                            command.CommandText = "select name from tutor_courses where email = ?email";

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    tutorCourses.Add(reader.GetString("name"));
                                }
                            }

                            String userToken = Guid.NewGuid().ToString();

                            command.CommandText = "INSERT INTO sessions VALUES (?userEmail, ?userToken)";
                            command.Parameters.AddWithValue("userToken", userToken);

                            if (command.ExecuteNonQuery() > 0)
                            {
                                VerifiedUserItem user = new VerifiedUserItem();
                                user.userEmail = returnedUserEmail;
                                user.userPassword = returnedUserPassword;
                                user.userStudentCourses = studentCourses;
                                user.userTutorCourses = tutorCourses;
                                user.userToken = userToken;

                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                return user;
                            }
                            else
                            {
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                return new VerifiedUserItem();
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        throw e;
                    }
                }
            }
        }

        public void MakeTutorAvailable(TutorUserItem data)
        {
            lock (this)
            {

                String userEmail = data.userEmail;
                String userToken = data.userToken;
                String tutorCourse = data.tutorCourse;
                String latitude = data.latitude;
                String longitude = data.longitude;

                String returnedUserEmail = "";
                String returnedCourseName = "";

                // Check that the user token is valid
                if (checkUserToken(userEmail, userToken))
                {
                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            // Verify the user is able to tutor the course specified 
                            MySqlCommand command = conn.CreateCommand();

                            command.CommandText = "SELECT * FROM tutor_courses WHERE email = ?userEmail AND name = ?tutorCourse";
                            command.Parameters.AddWithValue("userEmail", userEmail);
                            command.Parameters.AddWithValue("tutorCourse", tutorCourse);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedUserEmail = reader.GetString("email");
                                    returnedCourseName = reader.GetString("name");
                                }
                            }

                            if (userEmail == returnedUserEmail && tutorCourse == returnedCourseName)
                            {
                                command.CommandText = "INSERT INTO available_tutors VALUES (?userEmail, ?tutorCourse, ?latitude, ?longitude)";
                                command.Parameters.AddWithValue("latitude", latitude);
                                command.Parameters.AddWithValue("longitude", longitude);

                                if (command.ExecuteNonQuery() > 0)
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                }
                                else
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                                }
                            }
                            else
                            {
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                            }
                        }
                        catch (Exception e)
                        {
                            throw e;
                        }
                    }
                }
                else
                {
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                } 
            }
        }

        /// <summary>
        /// Method called to remove tutor from the available_tutor table.
        /// </summary>
        /// <param name="userEmail"></param>
        public void DeleteTutorAvailable(DeleteTutorUserItem data)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(data.userEmail, data.userToken))
                {
                    String returnedUserEmail = "";

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "SELECT email FROM available_tutors WHERE email = ?userEmail";
                            command.Parameters.AddWithValue("userEmail", data.userEmail);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedUserEmail = reader.GetString("email");
                                }
                            }

                            if (data.userEmail == returnedUserEmail)
                            {
                                command.CommandText = "DELETE FROM available_tutors WHERE email = ?userEmail";

                                if (command.ExecuteNonQuery() >= 0)
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                }
                                else
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                }
                            }
                            else
                            {
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                            }
                        }
                        catch (Exception e)
                        {
                            throw e;
                        }
                    }
                }
                else
                {
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                }
            }
        }

        public List<AvailableTutorUserItem> FindAvailableTutors(TutorUserItem item)
        {
            lock (this)
            {

                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    String returnedTutorEmail = "";
                    String returnedCourseName = "";
                    Double returnedTutorLatitude = 0;
                    Double returnedTutorLongitude = 0;

                    List<AvailableTutorUserItem> availableTutors = new List<AvailableTutorUserItem>();

                    var studentCoord = new GeoCoordinate(Convert.ToDouble(item.latitude), Convert.ToDouble(item.longitude));


                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "SELECT * FROM available_tutors WHERE course = ?courseName";
                            command.Parameters.AddWithValue("courseName", item.tutorCourse);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedTutorEmail = reader.GetString("email");
                                    returnedCourseName = reader.GetString("course");
                                    returnedTutorLatitude = reader.GetDouble("latitude");
                                    returnedTutorLongitude = reader.GetDouble("longitude");

                                    var tutorCoord = new GeoCoordinate(returnedTutorLatitude, returnedTutorLongitude);

                                    double distanceToTutor = studentCoord.GetDistanceTo(tutorCoord);

                                    if (distanceToTutor < 8046.72)
                                    {
                                        AvailableTutorUserItem tutor = new AvailableTutorUserItem();
                                        tutor.userEmail = returnedTutorEmail;
                                        tutor.tutorCourse = returnedCourseName;
                                        tutor.latitude = returnedTutorLatitude;
                                        tutor.longitude = returnedTutorLongitude;
                                        tutor.distanceFromStudent = distanceToTutor / 1609.34;

                                        availableTutors.Add(tutor);
                                    }
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.ServiceUnavailable;
                            throw e;
                        }
                    }

                    return availableTutors;
                }
                else
                {
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new List<AvailableTutorUserItem>();
                }
            }
        }

        public StudentTutorPairedItem PairStudentTutor(StudentTutorRequestItem item)
        {
            lock (this)
            {

                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Check that the tutor is still available 
                    String returnedTutorEmail = "";
                    String returnedCourseName = "";
                    String returnedTutorLatitude = "";
                    String returnedTutorLongitude = "";

                    String tutorEmail = item.requestedTutorEmail;

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "SELECT * FROM available_tutors WHERE email = ?tutorEmail";
                            command.Parameters.AddWithValue("tutorEmail", tutorEmail);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedTutorEmail = reader.GetString("email");
                                    returnedCourseName = reader.GetString("course");
                                    returnedTutorLatitude = reader.GetString("latitude");
                                    returnedTutorLongitude = reader.GetString("longitude");
                                }
                            }

                            if (returnedTutorEmail == item.requestedTutorEmail)
                            {
                                // Remove tutor from available_tutor table
                                command.CommandText = "DELETE FROM available_tutors WHERE email = ?tutorEmail";

                                if (command.ExecuteNonQuery() >= 0)
                                {
                                    // Insert student & tutor into the tutor_sesssion table with session status of 0 -> not started
                                    command.CommandText = "INSERT INTO tutor_sessions VALUES (?studentEmail, ?tutorEmail, ?course, ?studentLatitude, ?studentLongitude, ?tutorLatitude, ?tutorLongitude, ?session_status)";
                                    command.Parameters.AddWithValue("studentEmail", item.userEmail);
                                    command.Parameters.AddWithValue("course", returnedCourseName);
                                    command.Parameters.AddWithValue("studentLatitude", item.studentLatitude);
                                    command.Parameters.AddWithValue("studentLongitude", item.studentLongitude);
                                    command.Parameters.AddWithValue("tutorLatitude", returnedTutorLatitude);
                                    command.Parameters.AddWithValue("tutorLongitude", returnedTutorLongitude);
                                    command.Parameters.AddWithValue("session_status", 0);

                                    if (command.ExecuteNonQuery() > 0)
                                    {
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                        StudentTutorPairedItem paired = new StudentTutorPairedItem();
                                        paired.userEmail = item.userEmail;
                                        paired.userToken = item.userToken;
                                        paired.requestedTutorEmail = item.requestedTutorEmail;
                                        paired.tutorCourse = returnedCourseName;
                                        paired.studentLatitude = item.studentLatitude;
                                        paired.studentLongitude = item.studentLongitude;
                                        paired.tutorLatitude = returnedTutorLatitude;
                                        paired.tutorLongitude = returnedTutorLongitude;
                                        paired.session_status = 0;

                                        return paired;
                                    }
                                    else
                                    {
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadRequest;
                                        return new StudentTutorPairedItem();
                                    }
                                }
                                else
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                    return new StudentTutorPairedItem();
                                }
                            }
                            else
                            {
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Gone;
                                return new StudentTutorPairedItem();
                            }
                        }
                        catch (Exception e)
                        {
                            throw e;
                        }
                    }
                }
                else
                {
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new StudentTutorPairedItem();
                }
            }
        }


        private Boolean checkUserToken(String userEmail, String userToken)
        {
            using (MySqlConnection conn = new MySqlConnection(connectionString))
            {
                try
                {
                    conn.Open();

                    MySqlCommand command = conn.CreateCommand();
                    command.CommandText = "SELECT * FROM sessions WHERE email = ?userEmail";
                    command.Parameters.AddWithValue("userEmail", userEmail);

                    String returnedUserEmail = "";
                    String returnedUserToken = "";

                    using (MySqlDataReader reader = command.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            returnedUserEmail = reader.GetString("email");
                            returnedUserToken = reader.GetString("sessionToken");
                        }
                    }

                    if (returnedUserEmail == userEmail && returnedUserToken == userToken)
                        return true;
                    else
                        return false;
                }
                catch (Exception e)
                {
                    throw e;
                }
            }
        }
    }
}
