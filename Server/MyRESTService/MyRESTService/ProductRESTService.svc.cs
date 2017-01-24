using System;
using System.Collections.Generic;
using System.ServiceModel.Web;
using System.Net;
using System.Collections;
using System.Text;
using MySql.Data.MySqlClient;
using System.Device.Location;
using System.Security.Cryptography;


namespace ToDoList
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the class name "ProductRESTService" in code, svc and config file together.
    // NOTE: In order to launch WCF Test Client for testing this service, please select ProductRESTService.svc or ProductRESTService.svc.cs at the Solution Explorer and start debugging.
    public class ProductRESTService : IToDoService
    {
        //public const string connectionString = "Server=maria.eng.utah.edu;Port=3306;Database=tuber;UID=tobin;Password=traflip53";

        public const string connectionString = "Server=23.99.55.197;Database=tuber;UID=tobin;Password=Redpack!99!!";

        public List<Product> GetProductList()
        {
            return Products.Instance.ProductList;
        }

        public MakeUserItem CreateUser(CreateUserItem item)
        {
            lock(this)
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
                            command.CommandText = "INSERT INTO study_hotspots (owner_email, course_name, latitude, longitude, student_count) VALUES (?owner_email, ?course_name, ?latitude, ?longitude, 0)";
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
