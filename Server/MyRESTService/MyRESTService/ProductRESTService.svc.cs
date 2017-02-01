using System;
using System.Collections.Generic;
using System.ServiceModel.Web;
using System.Net;
using System.Collections;
using System.Text;
using MySql.Data.MySqlClient;
using System.Device.Location;
using System.Security.Cryptography;
using System.Globalization;


namespace ToDoList
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the class name "ProductRESTService" in code, svc and config file together.
    // NOTE: In order to launch WCF Test Client for testing this service, please select ProductRESTService.svc or ProductRESTService.svc.cs at the Solution Explorer and start debugging.
    public class ProductRESTService : IToDoService
    {
        // Active CADE DB
        //public const string connectionString = "Server=maria.eng.utah.edu;Port=3306;Database=tuber;UID=tobin;Password=traflip53";

        // Developmental DB
        public const string connectionString = "Server=sql3.freemysqlhosting.net;Port=3306;Database=sql3153117;UID=sql3153117;Password=vjbaNtDruW";

        // Old VM DB
        //public const string connectionString = "Server=23.99.55.197;Database=tuber;UID=tobin;Password=Redpack!99!!";

        public List<Product> GetProductList()
        {
            return Products.Instance.ProductList;
        }

        public MakeUserItem CreateUser(CreateUserItem item)
        {
            lock (this)
            {
                // Create password hash to store in DB
                String hashValue = computeHash(item.userPassword, null);

                // Store user information in DB
                using (MySqlConnection conn = new MySqlConnection(connectionString))
                {
                    try
                    {
                        conn.Open();

                        MySqlCommand command = conn.CreateCommand();
                        command.CommandText = "INSERT INTO users VALUES (?userEmail, ?hashValue, ?userFirstName, ?userLastName, ?userBillingAddress, ?userBillingCity, ?userBillingState, ?userBillingCCNumber, ?userBillingCCExpDate, ?userBillingCCV, 0)";
                        command.Parameters.AddWithValue("userEmail", item.userEmail);
                        command.Parameters.AddWithValue("hashValue", hashValue);
                        command.Parameters.AddWithValue("userFirstName", item.userFirstName);
                        command.Parameters.AddWithValue("userLastName", item.userLastName);
                        command.Parameters.AddWithValue("userBillingAddress", item.userBillingAddress);
                        command.Parameters.AddWithValue("userBillingCity", item.userBillingCity);
                        command.Parameters.AddWithValue("userBillingState", item.userBillingState);
                        command.Parameters.AddWithValue("userBillingCCNumber", item.userBillingCCNumber);
                        command.Parameters.AddWithValue("userBillingCCExpDate", item.userBillingCCExpDate);
                        command.Parameters.AddWithValue("userBillingCCV", item.userBillingCCV);

                        if (command.ExecuteNonQuery() > 0)
                        {
                            MakeUserItem user = new MakeUserItem();
                            user.userEmail = item.userEmail;
                            user.userPassword = item.userPassword;

                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;

                            return user;
                        }
                        else
                        {
                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadRequest;
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

        //public MakeUserItem MakeUser(UserItem item)
        //{
        //    lock (this)
        //    {
        //        String userEmail = item.userEmail;
        //        String userPassword = item.userPassword;

        //        using (MySqlConnection conn = new MySqlConnection(connectionString))
        //        {
        //            try
        //            {
        //                conn.Open();

        //                MySqlCommand command = conn.CreateCommand();
        //                command.CommandText = "insert into Users2 (userEmail, userPassword) values (?userEmail, ?userPassword)";
        //                command.Parameters.AddWithValue("userEmail", userEmail);
        //                command.Parameters.AddWithValue("userPassword", userPassword);

        //                if (command.ExecuteNonQuery() > 0)
        //                {
        //                    MakeUserItem user = new MakeUserItem();
        //                    user.userEmail = userEmail;
        //                    user.userPassword = userPassword;

        //                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;

        //                    return user;
        //                }
        //                else
        //                {
        //                    return new MakeUserItem();
        //                }
        //            }
        //            catch (Exception e)
        //            {
        //                throw e;
        //            }
        //        }
        //    }
        //}

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

                String returnedUserEmail = "";
                String returnedUserPassword = "";

                using (MySqlConnection conn = new MySqlConnection(connectionString))
                {
                    try
                    {
                        conn.Open();

                        MySqlCommand command = conn.CreateCommand();
                        command.CommandText = "select email, password from users where email = ?userEmail";
                        command.Parameters.AddWithValue("userEmail", data.userEmail);

                        using (MySqlDataReader reader = command.ExecuteReader())
                        {
                            while (reader.Read())
                            {
                                returnedUserEmail = reader.GetString("email");
                                returnedUserPassword = reader.GetString("password");
                            }
                        }

                        if (!verifyHash(data.userPassword, returnedUserPassword))
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
                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadGateway;
                        VerifiedUserItem user = new VerifiedUserItem();
                        user.userEmail = e.ToString();
                        return user;
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
                                    command.CommandText = "INSERT INTO tutor_sessions_pairing (studentEmail, tutorEmail, course, studentLatitude, studentLongitude, tutorLatitude, tutorLongitude) VALUES (?studentEmail, ?tutorEmail, ?course, ?studentLatitude, ?studentLongitude, ?tutorLatitude, ?tutorLongitude)";
                                    command.Parameters.AddWithValue("studentEmail", item.userEmail);
                                    command.Parameters.AddWithValue("course", returnedCourseName);
                                    command.Parameters.AddWithValue("studentLatitude", item.studentLatitude);
                                    command.Parameters.AddWithValue("studentLongitude", item.studentLongitude);
                                    command.Parameters.AddWithValue("tutorLatitude", returnedTutorLatitude);
                                    command.Parameters.AddWithValue("tutorLongitude", returnedTutorLongitude);

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

        public PairedStatusItem CheckPairedStatus(CheckPairedStatusItem item)
        {
            lock (this)
            {

                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Check that the tutor is still available 
                    String returnedTutorEmail = "";
                    String returnedStudentEmail = "";
                    String returnedTutorCourse = "";
                    String returnedStudentLatitude = "";
                    String returnedStudentLongitude = "";
                    String returnedTutorLatitude = "";
                    String returnedTutorLongitude = "";

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "SELECT * FROM available_tutors WHERE email = ?userEmail";
                            command.Parameters.AddWithValue("userEmail", item.userEmail);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedTutorEmail = reader.GetString("email");
                                }
                            }

                            if (returnedTutorEmail == "")
                            {
                                // Remove tutor from available_tutor table
                                command.CommandText = "SELECT * FROM tutor_sessions_pairing WHERE tutorEmail = ?userEmail";

                                PairedStatusItem pairedStatus = new PairedStatusItem();

                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        pairedStatus.studentEmail = reader.GetString("studentEmail");
                                        pairedStatus.userEmail = reader.GetString("tutorEmail");
                                        pairedStatus.tutorCourse = reader.GetString("course");
                                        pairedStatus.studentLatitude = reader.GetString("studentLatitude");
                                        pairedStatus.studentLongitude = reader.GetString("studentLongitude");
                                        pairedStatus.tutorLatitude = reader.GetString("tutorLatitude");
                                        pairedStatus.tutorLongitude = reader.GetString("tutorLongitude");
                                        //pairedStatus.session_status = reader.GetInt32("session_status");
                                    }
                                }

                                if (pairedStatus.userEmail == "" || pairedStatus.userEmail == null)
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadRequest;
                                    return new PairedStatusItem();
                                }
                                else
                                {
                                    return pairedStatus;
                                }
                            }
                            else
                            {
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                return new PairedStatusItem();
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
                    return new PairedStatusItem();
                }
            }
        }

        public void StartTutorSession(StartTutorSessionItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Get info from tutor_sessions_pairing table
                    String returnedStudentEmail = "";
                    String returnedTutorEmail = "";
                    String returnedCourseName = "";

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "SELECT studentEmail, tutorEmail, course FROM tutor_sessions_pairing WHERE tutorEmail = ?tutorEmail";
                            command.Parameters.AddWithValue("tutorEmail", item.userEmail);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedStudentEmail = reader.GetString("studentEmail");
                                    returnedTutorEmail = reader.GetString("tutorEmail");
                                    returnedCourseName = reader.GetString("course");
                                }
                            }

                            if (returnedTutorEmail == item.userEmail)
                            {
                                // Remove tutor from available_tutor table
                                command.CommandText = "DELETE FROM tutor_sessions_pairing WHERE tutorEmail = ?tutorEmail";

                                if (command.ExecuteNonQuery() >= 0)
                                {
                                    // Insert student & tutor into the tutor_sesssions_active table
                                    command.CommandText = "INSERT INTO tutor_sessions_active VALUES (?studentEmail, ?tutorEmail, ?course, ?session_start_time)";
                                    command.Parameters.AddWithValue("studentEmail", returnedStudentEmail);
                                    command.Parameters.AddWithValue("course", returnedCourseName);
                                    command.Parameters.AddWithValue("session_start_time", DateTime.Now);

                                    if (command.ExecuteNonQuery() > 0)
                                    {
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                    }
                                    else
                                    {
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadRequest;
                                    }
                                }
                                else
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                }
                            }
                            else
                            {
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Gone;
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

        public EndTutorSessionResponseItem EndTutorSession(EndTutorSessionRequestItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Get info from tutor_sessions_active table
                    String returnedStudentEmail = "";
                    String returnedTutorEmail = "";
                    String returnedCourseName = "";
                    DateTime returnedSessionStartTime = DateTime.Now;
                    DateTime sessionEndTime = DateTime.Now;

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "SELECT * FROM tutor_sessions_active WHERE tutorEmail = ?tutorEmail";
                            command.Parameters.AddWithValue("tutorEmail", item.userEmail);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedStudentEmail = reader.GetString("studentEmail");
                                    returnedTutorEmail = reader.GetString("tutorEmail");
                                    returnedCourseName = reader.GetString("course");
                                    returnedSessionStartTime = reader.GetDateTime("session_start_time");
                                }
                            }

                            if (returnedTutorEmail == item.userEmail)
                            {
                                // Remove tutor from tutor_sessions_active table
                                command.CommandText = "DELETE FROM tutor_sessions_active WHERE tutorEmail = ?tutorEmail";

                                if (command.ExecuteNonQuery() >= 0)
                                {
                                    // Calculate the total cost of the tutoring session
                                    TimeSpan diff = sessionEndTime.Subtract(returnedSessionStartTime);
                                    double cost = diff.TotalMinutes * 0.25;
                                    cost = Math.Round(cost, 2);

                                    // Insert student & tutor into the tutor_sesssions_complete table
                                    command.CommandText = "INSERT INTO tutor_sessions_completed (studentEmail, tutorEmail, course, session_start_time, session_end_time, session_cost) VALUES (?studentEmail, ?tutorEmail, ?course, ?session_start_time, ?session_end_time, ?session_cost)";
                                    command.Parameters.AddWithValue("studentEmail", returnedStudentEmail);
                                    command.Parameters.AddWithValue("course", returnedCourseName);
                                    command.Parameters.AddWithValue("session_start_time", returnedSessionStartTime);
                                    command.Parameters.AddWithValue("session_end_time", sessionEndTime);
                                    command.Parameters.AddWithValue("session_cost", cost);

                                    if (command.ExecuteNonQuery() > 0)
                                    {
                                        int returnedTutorSessionID = -1;

                                        command.CommandText = "SELECT tutor_session_id FROM tutor_sessions_completed WHERE studentEmail = ?studentEmail AND tutorEmail = ?tutorEmail AND course = ?course AND session_start_time = ?session_start_time AND session_end_time = ?session_end_time AND session_cost = ?session_cost";

                                        using (MySqlDataReader reader = command.ExecuteReader())
                                        {
                                            while (reader.Read())
                                            {
                                                returnedTutorSessionID = reader.GetInt32("tutor_session_id");
                                            }
                                        }

                                        if (returnedTutorSessionID != -1)
                                        {
                                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                            EndTutorSessionResponseItem endresponse = new EndTutorSessionResponseItem();
                                            endresponse.tutorSessionID = returnedTutorSessionID;
                                            endresponse.userEmail = returnedTutorEmail;
                                            endresponse.studentEmail = returnedStudentEmail;
                                            endresponse.course = returnedCourseName;
                                            endresponse.sessionStartTime = returnedSessionStartTime.ToString();
                                            endresponse.sessionEndTime = sessionEndTime.ToString();
                                            endresponse.sessionCost = cost;
                                            return endresponse;
                                        }
                                        else
                                        {
                                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.ExpectationFailed;
                                            return new EndTutorSessionResponseItem();
                                        }
                                    }
                                    else
                                    {
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadRequest;
                                        return new EndTutorSessionResponseItem();
                                    }
                                }
                                else
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                    return new EndTutorSessionResponseItem();
                                }
                            }
                            else
                            {
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Gone;
                                return new EndTutorSessionResponseItem();
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
                    return new EndTutorSessionResponseItem();
                }
            }
        }

        public void RateTutor(RateTutorItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    String returnedStudentEmail = "";
                    String returnedTutorEmail = "";

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            // Check to see if student & tutor were involved in the specified session ID
                            MySqlCommand command = conn.CreateCommand();

                            command.CommandText = "SELECT tutorEmail FROM tutor_ratings WHERE tutor_session_id = ?tutorSessionID";
                            command.Parameters.AddWithValue("tutorSessionID", item.tutorSessionID);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedTutorEmail = reader.GetString("tutorEmail");
                                }
                            }

                            if (returnedTutorEmail == "" || returnedTutorEmail == null)
                            {
                                command.CommandText = "SELECT studentEmail, tutorEmail FROM tutor_sessions_completed WHERE tutor_session_id = ?tutorSessionID";

                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        returnedStudentEmail = reader.GetString("studentEmail");
                                        returnedTutorEmail = reader.GetString("tutorEmail");
                                    }
                                }

                                if (returnedStudentEmail == item.userEmail && returnedTutorEmail == item.tutorEmail)
                                {

                                    command.CommandText = "INSERT INTO tutor_ratings VALUES (?tutorSessionID, ?tutorEmail, ?studentEmail, ?rating)";
                                    command.Parameters.AddWithValue("studentEmail", returnedStudentEmail);
                                    command.Parameters.AddWithValue("tutorEmail", returnedTutorEmail);
                                    command.Parameters.AddWithValue("rating", item.rating);

                                    if (command.ExecuteNonQuery() > 0)
                                    {
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                    }
                                    else
                                    {
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.ExpectationFailed;
                                    }
                                }
                                else
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                }
                            }
                            else
                            {
                                // There is already a record in the tutor_ratings table for  this session ID
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.NotAcceptable;
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

        public void RateStudent(RateStudentItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    String returnedStudentEmail = "";
                    String returnedTutorEmail = "";

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            // Check to see if student & tutor were involved in the specified session ID
                            MySqlCommand command = conn.CreateCommand();

                            command.CommandText = "SELECT studentEmail FROM student_ratings WHERE tutor_session_id = ?tutorSessionID";
                            command.Parameters.AddWithValue("tutorSessionID", item.tutorSessionID);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedStudentEmail = reader.GetString("studentEmail");
                                }
                            }

                            if (returnedStudentEmail == "" || returnedStudentEmail == null)
                            {
                                command.CommandText = "SELECT studentEmail, tutorEmail FROM tutor_sessions_completed WHERE tutor_session_id = ?tutorSessionID";

                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        returnedStudentEmail = reader.GetString("studentEmail");
                                        returnedTutorEmail = reader.GetString("tutorEmail");
                                    }
                                }

                                if (returnedStudentEmail == item.studentEmail && returnedTutorEmail == item.userEmail)
                                {

                                    command.CommandText = "INSERT INTO student_ratings VALUES (?tutorSessionID, ?tutorEmail, ?studentEmail, ?rating)";
                                    command.Parameters.AddWithValue("studentEmail", returnedStudentEmail);
                                    command.Parameters.AddWithValue("tutorEmail", returnedTutorEmail);
                                    command.Parameters.AddWithValue("rating", item.rating);

                                    if (command.ExecuteNonQuery() > 0)
                                    {
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                    }
                                    else
                                    {
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.ExpectationFailed;
                                    }
                                }
                                else
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                }
                            }
                            else
                            {
                                // There is already a record in the tutor_ratings table for  this session ID
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.NotAcceptable;
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

        public void CreateStudyHotspot(CreateStudyHotspotRequestItem item)
        {
            lock (this)
            {

                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {

                    String returnedHotspotID = "";

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "INSERT INTO study_hotspots (owner_email, course_name, latitude, longitude, student_count) VALUES (?owner_email, ?course_name, ?latitude, ?longitude, 1)";
                            command.Parameters.AddWithValue("owner_email", item.userEmail);
                            command.Parameters.AddWithValue("course_name", item.course);
                            command.Parameters.AddWithValue("latitude", item.latitude);
                            command.Parameters.AddWithValue("longitude", item.longitude);

                            if (command.ExecuteNonQuery() > 0)
                            {
                                command.CommandText = "SELECT hotspot_id FROM study_hotspots WHERE owner_email = ?owner_email";
                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        returnedHotspotID = reader.GetString("hotspot_id");
                                    }
                                }

                                command.CommandText = "INSERT INTO study_hotspots_members (hotspot_id, email) VALUES (?hotspot_id, ?email)";
                                command.Parameters.AddWithValue("email", item.userEmail);
                                command.Parameters.AddWithValue("hotspot_id", returnedHotspotID);

                                if (command.ExecuteNonQuery() > 0)
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
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
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

        public List<AvailableStudyHotspotItem> FindStudyHotspots(StudyHotspotItem item)
        {
            lock (this)
            {

                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    String returnedHotspotID = "";
                    String returnedOwnerEmail = "";
                    String returnedCourseName = "";
                    Double returnedHotspotLatitude = 0;
                    Double returnedHotspotLongitude = 0;
                    String returnedStudentCount = "";

                    List<AvailableStudyHotspotItem> availableHotspots = new List<AvailableStudyHotspotItem>();

                    var studentCoord = new GeoCoordinate(Convert.ToDouble(item.latitude), Convert.ToDouble(item.longitude));


                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "SELECT * FROM study_hotspots WHERE course_name = ?courseName";
                            command.Parameters.AddWithValue("courseName", item.course);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedHotspotID = reader.GetString("hotspot_id");
                                    returnedOwnerEmail = reader.GetString("owner_email");
                                    returnedCourseName = reader.GetString("course_name");
                                    returnedHotspotLatitude = reader.GetDouble("latitude");
                                    returnedHotspotLongitude = reader.GetDouble("longitude");
                                    returnedStudentCount = reader.GetString("student_count");

                                    var hotspotCoord = new GeoCoordinate(returnedHotspotLatitude, returnedHotspotLongitude);

                                    double distanceToHotspot = studentCoord.GetDistanceTo(hotspotCoord);

                                    if (distanceToHotspot < 8046.72)
                                    {
                                        AvailableStudyHotspotItem hotspot = new AvailableStudyHotspotItem();
                                        hotspot.hotspotID = returnedHotspotID;
                                        hotspot.ownerEmail = returnedOwnerEmail;
                                        hotspot.course = returnedCourseName;
                                        hotspot.latitude = returnedHotspotLatitude;
                                        hotspot.longitude = returnedHotspotLongitude;
                                        hotspot.student_count = returnedStudentCount;
                                        hotspot.distanceToHotspot = distanceToHotspot / 1609.34;

                                        availableHotspots.Add(hotspot);
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

                    return availableHotspots;
                }
                else
                {
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new List<AvailableStudyHotspotItem>();
                }
            }
        }

        public void JoinStudyHotspot(StudyHotspotJoinItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {

                    String returnedHotspotID = "";

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            // Check to see if hotspot still exists 
                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "SELECT hotspot_id FROM study_hotspots WHERE hotspot_id = ?hotspotID";
                            command.Parameters.AddWithValue("hotspotID", item.hotspotID);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedHotspotID = reader.GetString("hotspot_id");
                                }
                            }

                            if (returnedHotspotID == item.hotspotID)
                            {
                                // Insert user into hotspot 
                                command.CommandText = "INSERT INTO study_hotspots_members (hotspot_id, email) VALUES (?hotspotID, ?email)";
                                command.Parameters.AddWithValue("email", item.userEmail);

                                if (command.ExecuteNonQuery() > 0)
                                {
                                    // Update hotspot member count
                                    int returnedStudentCount = 0;

                                    command.CommandText = "SELECT student_count FROM study_hotspots WHERE hotspot_id = ?hotspotID";
                                    using (MySqlDataReader reader = command.ExecuteReader())
                                    {
                                        while (reader.Read())
                                        {
                                            returnedStudentCount = reader.GetInt32("student_count");
                                        }
                                    }

                                    command.CommandText = "UPDATE study_hotspots SET student_count = ?studentCount where hotspot_id = ?hotspotID;";
                                    command.Parameters.AddWithValue("studentCount", returnedStudentCount + 1);

                                    if (command.ExecuteNonQuery() > 0)
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
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                }
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

        public void LeaveStudyHotspot(StudyHotspotLeaveItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {

                    String returnedHotspotID = "";

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            // Get the ID of the hotspot the user is leaving
                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "SELECT hotspot_id FROM study_hotspots_members WHERE email = ?email";
                            command.Parameters.AddWithValue("email", item.userEmail);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedHotspotID = reader.GetString("hotspot_id");
                                }
                            }

                            command.CommandText = "DELETE FROM study_hotspots_members WHERE hotspot_id = ?hotspotID AND email = ?email";
                            command.Parameters.AddWithValue("hotspotID", returnedHotspotID);

                            if (command.ExecuteNonQuery() > 0)
                            {
                                // Update hotspot member count
                                int returnedStudentCount = 0;

                                command.CommandText = "SELECT student_count FROM study_hotspots WHERE hotspot_id = ?hotspotID";
                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        returnedStudentCount = reader.GetInt32("student_count");
                                    }
                                }

                                command.CommandText = "UPDATE study_hotspots SET student_count = ?studentCount where hotspot_id = ?hotspotID;";
                                command.Parameters.AddWithValue("studentCount", returnedStudentCount - 1);

                                if (command.ExecuteNonQuery() > 0)
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                }
                                else
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                }
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

        public List<StudyHotspotMemberItem> GetStudyHotspotMembers(StudyHotspotGetMemberItem item)
        {
            lock (this)
            {

                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    String returnedEmail = "";
                    String returnedFirstName = "";
                    String returnedLastName = "";

                    List<String> memberEmails = new List<String>();

                    List<StudyHotspotMemberItem> hotspotMembers = new List<StudyHotspotMemberItem>();

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "SELECT email FROM study_hotspots_members WHERE hotspot_id = ?hotspotID";
                            command.Parameters.AddWithValue("hotspotID", item.hotspotID);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedEmail = reader.GetString("email");
                                    memberEmails.Add(returnedEmail);
                                }
                            }

                            for (int i = 0; i < memberEmails.Count; i++)
                            {
                                command.CommandText = "SELECT first_name, last_name FROM users WHERE email = ?email";
                                command.Parameters.Clear();
                                command.Parameters.AddWithValue("email", memberEmails[i]);

                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        returnedFirstName = reader.GetString("first_name");
                                        returnedLastName = reader.GetString("last_name");
                                    }
                                }

                                StudyHotspotMemberItem member = new StudyHotspotMemberItem();
                                member.userEmail = memberEmails[i];
                                member.firstName = returnedFirstName;
                                member.lastName = returnedLastName;

                                hotspotMembers.Add(member);
                            }
                        }
                        catch (Exception e)
                        {
                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.ServiceUnavailable;
                            throw e;
                        }
                    }

                    return hotspotMembers;
                }
                else
                {
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new List<StudyHotspotMemberItem>();
                }
            }
        }

        public void DeleteStudyHotspot(StudyHotspotDeleteItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    String returnedUserEmail = "";
                    String returnedMemberEmail = "";

                    List<String> memberEmails = new List<String>();

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            // Check to see if the user owns the hotspot 
                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "SELECT owner_email FROM study_hotspots WHERE hotspot_id = ?hotspotID";
                            command.Parameters.AddWithValue("hotspotID", item.hotspotID);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedUserEmail = reader.GetString("owner_email");
                                }
                            }

                            if (returnedUserEmail == item.userEmail)
                            {
                                // Remove all members from the hotspot
                                command.CommandText = "SELECT email FROM study_hotspots_members WHERE hotspot_id = ?hotspotID";

                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        returnedMemberEmail = reader.GetString("email");
                                        memberEmails.Add(returnedMemberEmail);
                                    }
                                }

                                for (int i = 0; i < memberEmails.Count; i++)
                                {
                                    command.CommandText = "DELETE FROM study_hotspots_members WHERE email = ?email";
                                    command.Parameters.Clear();
                                    command.Parameters.AddWithValue("email", memberEmails[i]);

                                    if (command.ExecuteNonQuery() > 0)
                                    {
                                        continue;
                                    }
                                    else
                                    {
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                        break;
                                    }
                                }

                                //  Remove delete the study hotspot
                                command.CommandText = "DELETE FROM study_hotspots WHERE hotspot_id = ?hotspotID";
                                if (command.ExecuteNonQuery() > 0)
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                }
                                else
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.ServiceUnavailable;
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

        public void ScheduleTutor(ScheduleTutorItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {

                    // Store student's tutor request in DB
                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "INSERT INTO tutor_requests VALUES (?studentEmail, ?course, ?topic, ?date, ?time, ?duration)";
                            command.Parameters.AddWithValue("studentEmail", item.userEmail);
                            command.Parameters.AddWithValue("course", item.course);
                            command.Parameters.AddWithValue("topic", item.topic);
                            command.Parameters.AddWithValue("date", item.date);
                            command.Parameters.AddWithValue("time", item.time);
                            command.Parameters.AddWithValue("duration", item.duration);

                            if (command.ExecuteNonQuery() > 0)
                            {
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                            }
                            else
                            {
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadRequest;
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

        public List<ScheduleTutorRequestItem> FindAllScheduleTutorRequests(FindAllScheduleTutorRequestItem item)
        {
            lock (this)
            {

                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    String returnedStudentEmail = "";
                    String returnedCourse = "";
                    String returnedTopic = "";
                    String returnedDate = "";
                    String returnedTime = "";
                    String returnedDuration = "";

                    List<ScheduleTutorRequestItem> studentRequests = new List<ScheduleTutorRequestItem>();

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "SELECT * FROM tutor_requests WHERE course = ?courseName";
                            command.Parameters.AddWithValue("courseName", item.course);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedStudentEmail = reader.GetString("student_email");
                                    returnedCourse = reader.GetString("course");
                                    returnedTopic = reader.GetString("topic");
                                    returnedDate = reader.GetString("date");
                                    returnedTime = reader.GetString("time");
                                    returnedDuration = reader.GetString("duration");

                                    ScheduleTutorRequestItem request = new ScheduleTutorRequestItem();
                                    request.studentEmail = returnedStudentEmail;
                                    request.course = returnedCourse;
                                    request.topic = returnedTopic;
                                    request.date = returnedDate;
                                    request.time = returnedTime;
                                    request.duration = returnedDuration;

                                    studentRequests.Add(request);
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.ServiceUnavailable;
                            throw e;
                        }
                    }

                    return studentRequests;
                }
                else
                {
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new List<ScheduleTutorRequestItem>();
                }
            }
        }

        public AcceptStudentScheduleRequestResponseItem AcceptStudentScheduledRequest(AcceptStudentScheduleRequestItem item)
        {
            lock (this)
            {

                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Check that the tutor is still available 
                    String returnedStudentEmail = "";
                    String returnedCourseName = "";
                    String returnedTopic = "";
                    String returnedDate = "";
                    String returnedTime = "";
                    String returnedDuration = "";

                    //String tutorEmail = item.requestedTutorEmail;

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "SELECT * FROM tutor_requests WHERE student_email = ?studentEmail AND course = ?course";
                            command.Parameters.AddWithValue("studentEmail", item.studentEmail);
                            command.Parameters.AddWithValue("course", item.course);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedStudentEmail = reader.GetString("student_email");
                                    returnedCourseName = reader.GetString("course");
                                    returnedTopic = reader.GetString("topic");
                                    returnedDate = reader.GetString("date");
                                    //returnedDate = reader.GetDateTime("date").ToString();
                                    returnedTime = reader.GetString("time");
                                    returnedDuration = reader.GetString("duration");
                                }
                            }

                            if (returnedStudentEmail == item.studentEmail && returnedCourseName == item.course)
                            {
                                // Fix date string to be in format yyyy-MM-dd
                                returnedDate = returnedDate.Split(' ')[0];
                                returnedDate = returnedDate.Replace(',', '-');
                                DateTime date = DateTime.ParseExact(returnedDate, "MM-dd-yyyy", CultureInfo.InvariantCulture);
                                returnedDate = date.ToString("yyyy-MM-dd");

                                // Remove tutor from available_tutor table
                                command.CommandText = "DELETE FROM tutor_requests WHERE student_email = ?studentEmail AND course = ?course";

                                if (command.ExecuteNonQuery() >= 0)
                                {
                                    command.CommandText = "INSERT INTO tutor_requests_accepted VALUES (?student_email, ?tutor_email, ?course, ?topic, ?date, ?time, ?duration)";
                                    command.Parameters.Clear();
                                    command.Parameters.AddWithValue("student_email", item.studentEmail);
                                    command.Parameters.AddWithValue("tutor_email", item.userEmail);
                                    command.Parameters.AddWithValue("course", returnedCourseName);
                                    command.Parameters.AddWithValue("topic", returnedTopic);
                                    command.Parameters.AddWithValue("date", returnedDate);
                                    command.Parameters.AddWithValue("time", returnedTime);
                                    command.Parameters.AddWithValue("duration", returnedDuration);

                                    if (command.ExecuteNonQuery() > 0)
                                    {
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                        AcceptStudentScheduleRequestResponseItem paired = new AcceptStudentScheduleRequestResponseItem();
                                        paired.student_email = item.studentEmail;
                                        paired.tutor_email = item.userEmail;
                                        paired.course = returnedCourseName;
                                        paired.topic = returnedTopic;
                                        paired.date = returnedDate;
                                        paired.time = returnedTime;
                                        paired.duration = returnedDuration;
                              
                                        return paired;
                                    }
                                    else
                                    {
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadRequest;
                                        return new AcceptStudentScheduleRequestResponseItem();
                                    }
                                }
                                else
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                    return new AcceptStudentScheduleRequestResponseItem();
                                }
                            }
                            else
                            {
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Gone;
                                return new AcceptStudentScheduleRequestResponseItem();
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
                    return new AcceptStudentScheduleRequestResponseItem();
                }
            }
        }

        public List<PairedScheduledStatusItem> CheckScheduledPairedStatus(CheckPairedStatusItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    List<PairedScheduledStatusItem> listings = new List<PairedScheduledStatusItem>();

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            // Check tutor_requests table for pending requests
                            MySqlCommand command = conn.CreateCommand();
                            command.CommandText = "SELECT * FROM tutor_requests WHERE student_email = ?userEmail";
                            command.Parameters.AddWithValue("userEmail", item.userEmail);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    PairedScheduledStatusItem statusItem = new PairedScheduledStatusItem();
                                    statusItem.studentEmail = reader.GetString("student_email");
                                    statusItem.course = reader.GetString("course");
                                    statusItem.topic = reader.GetString("topic");
                                    statusItem.date = reader.GetString("date").Split(' ')[0];
                                    statusItem.time = reader.GetString("time");
                                    statusItem.duration = reader.GetString("duration");
                                    statusItem.isPaired = false;
                                    listings.Add(statusItem);
                                }
                            }

                            // Check tutor_requests_accepted table for accepted requests
                            command.CommandText = "SELECT * FROM tutor_requests_accepted WHERE student_email = ?userEmail";

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    PairedScheduledStatusItem statusItem = new PairedScheduledStatusItem();
                                    statusItem.studentEmail = reader.GetString("student_email");
                                    statusItem.tutorEmail = reader.GetString("tutor_email");
                                    statusItem.course = reader.GetString("course");
                                    statusItem.topic = reader.GetString("topic");
                                    statusItem.date = reader.GetString("date").Split(' ')[0];
                                    statusItem.time = reader.GetString("time");
                                    statusItem.duration = reader.GetString("duration");
                                    statusItem.isPaired = true;
                                    listings.Add(statusItem);
                                }
                            }

                            return listings;
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
                    return new List<PairedScheduledStatusItem>();
                }
            }            
        }


        ////////////////////
        // Helper Functions 
        ////////////////////

        private string computeHash(String password, byte[] saltBytes)
        {
            // If no salt, then create it
            if (saltBytes == null)
            {
                // Min and max size for salt array
                int minSaltSize = 4;
                int maxSaltSize = 8;

                // Generate a random number to determine the salt size
                Random random = new Random();
                int saltSize = random.Next(minSaltSize, maxSaltSize);

                // Create the salt byte array
                saltBytes = new byte[saltSize];

                // Fill the salt array
                RNGCryptoServiceProvider rng = new RNGCryptoServiceProvider();
                rng.GetNonZeroBytes(saltBytes);
            }
            // Convert password string into byte array
            byte[] plainTextBytes = Encoding.UTF8.GetBytes(password);

            // Create array to hold the plainTextBytes and saltBytes
            byte[] plainTextWithSaltBytes = new byte[plainTextBytes.Length + saltBytes.Length];

            // Copy plain text bytes into plainTextWithSaltBytes array
            for (int i = 0; i < plainTextBytes.Length; i++)
            {
                plainTextWithSaltBytes[i] = plainTextBytes[i];
            }

            // Copy salt bytes into end of plainTextWithSaltBytes array
            for (int i = 0; i < saltBytes.Length; i++)
            {
                plainTextWithSaltBytes[plainTextBytes.Length + i] = saltBytes[i];
            }

            // Create hash function
            HashAlgorithm hash = new SHA256Managed();

            // Create hash of plainTextWithSaltBytes array
            byte[] hashBytes = hash.ComputeHash(plainTextWithSaltBytes);

            // Create array to hold hash and original salt bytes
            byte[] hashWithSaltBytes = new byte[hashBytes.Length + saltBytes.Length];

            // Copy hash bytes into hashWithSaltBytes array
            for (int i = 0; i < hashBytes.Length; i++)
            {
                hashWithSaltBytes[i] = hashBytes[i];
            }

            // Copy salt bytes into hashWithSaltBytes array
            for (int i = 0; i < saltBytes.Length; i++)
            {
                hashWithSaltBytes[hashBytes.Length + i] = saltBytes[i];
            }

            // Convert hashWithSaltBytes into a base64-encoded string
            String hashValue = Convert.ToBase64String(hashWithSaltBytes);

            return hashValue;
        }

        private Boolean verifyHash(String password, String hashFromDB)
        {
            // Convert base64-encoded hash value into byte array
            byte[] hashWithSaltBytes = Convert.FromBase64String(hashFromDB);

            // Keeps track of hash size in bits and bytes
            int hashSizeInBits = 256;
            int hashSizeInBytes = 32;

            // Create array to hold origianl salt bytes from hash
            byte[] saltBytes = new byte[hashWithSaltBytes.Length - 32];

            // Copy salt from hash to saltBytes array
            for (int i = 0; i < saltBytes.Length; i++)
            {
                saltBytes[i] = hashWithSaltBytes[32 + i];
            }

            // Compute new hash string
            String expectedHashString = computeHash(password, saltBytes);

            // Make sure the hash from the DB and newly computed hash match
            return (hashFromDB == expectedHashString);
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
