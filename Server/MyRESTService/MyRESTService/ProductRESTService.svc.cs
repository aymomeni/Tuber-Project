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
        public const string connectionString = "Server=maria.eng.utah.edu;Port=3306;Database=tuber;UID=tobin;Password=traflip53";

        // Developmental DB
        //public const string connectionString = "Server=sql3.freemysqlhosting.net;Port=3306;Database=sql3153117;UID=sql3153117;Password=vjbaNtDruW";

        // Old VM DB
        //public const string connectionString = "Server=23.99.55.197;Database=tuber;UID=tobin;Password=Redpack!99!!";

        //public List<Product> GetProductList()
        //{
        //    return Products.Instance.ProductList;
        //}

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
                        MakeUserItem user = new MakeUserItem();
                        user.userEmail = e.ToString();
                        return user;
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
        public VerifiedUserItem VerifyUser(UserItem item)
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
                        command.Parameters.AddWithValue("userEmail", item.userEmail);

                        using (MySqlDataReader reader = command.ExecuteReader())
                        {
                            while (reader.Read())
                            {
                                returnedUserEmail = reader.GetString("email");
                                returnedUserPassword = reader.GetString("password");
                            }
                        }

                        if (!verifyHash(item.userPassword, returnedUserPassword))
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

                            // Verify the user doesn't already have a session token, if they do, delete it and give new session token.
                            String existingSessionEmail = "";

                            command.CommandText = "SELECT email FROM sessions WHERE email = ?email";

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    existingSessionEmail = reader.GetString("email");
                                }
                            }

                            if (existingSessionEmail == item.userEmail)
                            {
                                command.CommandText = "DELETE FROM sessions WHERE email = ?userEmail";

                                if (command.ExecuteNonQuery() >= 0)
                                {
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
                                else
                                {
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                    return new VerifiedUserItem();
                                }
                            }
                            else
                            {
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

        public MakeTutorAvailableResponseItem MakeTutorAvailable(TutorUserItem item)
        {
            lock (this)
            {
                String returnedUserEmail = "";
                String returnedCourseName = "";

                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Make sure tutor is eligible to tutor
                    if (checkTutorEligibility(item.userEmail))
                    {
                        using (MySqlConnection conn = new MySqlConnection(connectionString))
                        {
                            try
                            {
                                conn.Open();

                                MySqlCommand command = conn.CreateCommand();

                                // Verify the user is able to tutor the course specified 
                                command.CommandText = "SELECT * FROM tutor_courses WHERE email = ?userEmail AND name = ?tutorCourse";
                                command.Parameters.AddWithValue("userEmail", item.userEmail);
                                command.Parameters.AddWithValue("tutorCourse", item.tutorCourse);

                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        returnedUserEmail = reader.GetString("email");
                                        returnedCourseName = reader.GetString("name");
                                    }
                                }

                                if (item.userEmail == returnedUserEmail && item.tutorCourse == returnedCourseName)
                                {
                                    // Insert tutor into the available_tutors table
                                    command.CommandText = "INSERT INTO available_tutors VALUES (?userEmail, ?tutorCourse, ?latitude, ?longitude)";
                                    command.Parameters.AddWithValue("latitude", item.latitude);
                                    command.Parameters.AddWithValue("longitude", item.longitude);

                                    if (command.ExecuteNonQuery() > 0)
                                    {
                                        // Insertion happend as expected
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                        return new MakeTutorAvailableResponseItem();
                                    }
                                    else
                                    {
                                        // Something went wrong inserting user into available_tutors
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                                        return new MakeTutorAvailableResponseItem();
                                    }
                                }
                                else
                                {
                                    // User does not have ability to tutor the class specified
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                                    return new MakeTutorAvailableResponseItem();
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
                        // User has tutor_eligible set to 0 -- not able to tutor any class
                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                        return new MakeTutorAvailableResponseItem();
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new MakeTutorAvailableResponseItem();
                }
            }
        }

        /// <summary>
        /// Method called to remove tutor from the available_tutor table.
        /// </summary>
        /// <param name="userEmail"></param>
        public DeleteTutorResponseItem DeleteTutorAvailable(DeleteTutorUserItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Make sure tutor is eligible to tutor
                    if (checkTutorEligibility(item.userEmail))
                    {
                        String returnedUserEmail = "";

                        using (MySqlConnection conn = new MySqlConnection(connectionString))
                        {
                            try
                            {
                                conn.Open();

                                MySqlCommand command = conn.CreateCommand();

                                // Verify the user to be deleted is in the available_tutors table
                                command.CommandText = "SELECT email FROM available_tutors WHERE email = ?userEmail";
                                command.Parameters.AddWithValue("userEmail", item.userEmail);

                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        returnedUserEmail = reader.GetString("email");
                                    }
                                }

                                if (item.userEmail == returnedUserEmail)
                                {
                                    // If user is in the available_tutors table, delete them from it
                                    command.CommandText = "DELETE FROM available_tutors WHERE email = ?userEmail";

                                    if (command.ExecuteNonQuery() >= 0)
                                    {
                                        // Deletion happened as expected
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                        return new DeleteTutorResponseItem();
                                    }
                                    else
                                    {
                                        // Something went wrong deleting user from available_tutors
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                        return new DeleteTutorResponseItem();
                                    }
                                }
                                else
                                {
                                    // User is not in the available_tutors table
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                                    return new DeleteTutorResponseItem();
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
                        // User has tutor_eligible set to 0 -- not able to tutor any class
                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                        return new DeleteTutorResponseItem();
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new DeleteTutorResponseItem();
                }
            }
        }

        public FindAvailableTutorResponseItem FindAvailableTutors(TutorUserItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    String returnedStudentEmail = "";
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

                            // Verify student is in the class provided
                            command.CommandText = "SELECT email FROM student_courses WHERE name = ?courseName";
                            command.Parameters.AddWithValue("courseName", item.tutorCourse);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedStudentEmail = reader.GetString("email");
                                }
                            }

                            // If student is in class provided, return list of available tutors
                            if (returnedStudentEmail == item.userEmail)
                            {
                                command.CommandText = "SELECT * FROM available_tutors WHERE course = ?courseName";

                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        returnedTutorEmail = reader.GetString("email");
                                        returnedCourseName = reader.GetString("course");
                                        returnedTutorLatitude = reader.GetDouble("latitude");
                                        returnedTutorLongitude = reader.GetDouble("longitude");

                                        var tutorCoord = new GeoCoordinate(returnedTutorLatitude, returnedTutorLongitude);

                                        // Calculate distance between tutor and student
                                        double distanceToTutor = studentCoord.GetDistanceTo(tutorCoord);

                                        // Only return tutors that are less than 5 miles from the student
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
                            else
                            {
                                // Student is not in the class provided
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                return new FindAvailableTutorResponseItem();
                            }
                        }
                        catch (Exception e)
                        {
                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.ServiceUnavailable;
                            throw e;
                        }
                    }

                    // Return list of available tutors
                    FindAvailableTutorResponseItem tutorList = new FindAvailableTutorResponseItem();
                    tutorList.availableTutors = availableTutors;
                    return tutorList;
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new FindAvailableTutorResponseItem();
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
                    String returnedTutorEmail = "";
                    String returnedCourseName = "";
                    String returnedTutorLatitude = "";
                    String returnedTutorLongitude = "";

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();

                            // Check that the tutor is still available 
                            command.CommandText = "SELECT * FROM available_tutors WHERE email = ?tutorEmail";
                            command.Parameters.AddWithValue("tutorEmail", item.requestedTutorEmail);

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
                                    // Insert student & tutor into the tutor_sesssion_pairing table 
                                    command.CommandText = "INSERT INTO tutor_sessions_pairing (studentEmail, tutorEmail, course, studentLatitude, studentLongitude, tutorLatitude, tutorLongitude) VALUES (?studentEmail, ?tutorEmail, ?course, ?studentLatitude, ?studentLongitude, ?tutorLatitude, ?tutorLongitude)";
                                    command.Parameters.AddWithValue("studentEmail", item.userEmail);
                                    command.Parameters.AddWithValue("course", returnedCourseName);
                                    command.Parameters.AddWithValue("studentLatitude", item.studentLatitude);
                                    command.Parameters.AddWithValue("studentLongitude", item.studentLongitude);
                                    command.Parameters.AddWithValue("tutorLatitude", returnedTutorLatitude);
                                    command.Parameters.AddWithValue("tutorLongitude", returnedTutorLongitude);

                                    if (command.ExecuteNonQuery() > 0)
                                    {
                                        // Return the paired object
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
                                        // Inserting into tutor_session_pairing table failed
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadRequest;
                                        return new StudentTutorPairedItem();
                                    }
                                }
                                else
                                {
                                    // Deleting from the available_tutors table failed
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                    return new StudentTutorPairedItem();
                                }
                            }
                            else
                            {
                                // Tutor is no longer available to pair
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
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new StudentTutorPairedItem();
                }
            }
        }

        public PairedStatusItem CheckPairedStatus(CheckPairedStatusItem item)
        {
            lock (this)
            {
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Make sure tutor is eligible to tutor
                    if (checkTutorEligibility(item.userEmail))
                    {
                        // Check that the tutor is still available 
                        String returnedTutorEmail = "";

                        using (MySqlConnection conn = new MySqlConnection(connectionString))
                        {
                            try
                            {
                                conn.Open();

                                MySqlCommand command = conn.CreateCommand();

                                // Check to see if the tutor is still in the available_tutors table
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
                                    // Check to see if the tutor is in the tutor_sessions_pairing table
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
                                        }
                                    }

                                    if (pairedStatus.userEmail == "" || pairedStatus.userEmail == null)
                                    {
                                        // Tutor was not in the available_tutor or tutor_sessions_pairing table, the tutor shouldn't be calling this method
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadRequest;
                                        return new PairedStatusItem();
                                    }
                                    else
                                    {
                                        // Found the tutor in the tutor_sessions_pairing table -- send back the paired student-tutor object for the tutor app to update
                                        return pairedStatus;
                                    }
                                }
                                else
                                {
                                    // Tutor is still waiting for a student to pair with them
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
                        // User has tutor_eligible set to 0 -- not able to tutor any class
                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                        return new PairedStatusItem();
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new PairedStatusItem();
                }
            }
        }

        public StartTutorSessionResponseItem StartTutorSession(StartTutorSessionItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Make sure tutor is eligible to tutor
                    if (checkTutorEligibility(item.userEmail))
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
                                    // Remove pairing from the tutor_sessions_pairing table
                                    command.CommandText = "DELETE FROM tutor_sessions_pairing WHERE tutorEmail = ?tutorEmail";

                                    if (command.ExecuteNonQuery() >= 0)
                                    {
                                        // Insert pairing into the tutor_sesssions_active table
                                        command.CommandText = "INSERT INTO tutor_sessions_active VALUES (?studentEmail, ?tutorEmail, ?course, ?session_start_time)";
                                        command.Parameters.AddWithValue("studentEmail", returnedStudentEmail);
                                        command.Parameters.AddWithValue("course", returnedCourseName);
                                        command.Parameters.AddWithValue("session_start_time", DateTime.Now);

                                        if (command.ExecuteNonQuery() > 0)
                                        {
                                            // Everything went as planned
                                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                            return new StartTutorSessionResponseItem();
                                        }
                                        else
                                        {
                                            // Inserting into tutor_sessions_active table failed
                                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadRequest;
                                            return new StartTutorSessionResponseItem();
                                        }
                                    }
                                    else
                                    {
                                        // Deleting from tutor_sessions_pairing failed
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                        return new StartTutorSessionResponseItem();
                                    }
                                }
                                else
                                {
                                    // Pairing session the tutor is looking for is no longer available. 
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Gone;
                                    return new StartTutorSessionResponseItem();
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
                        // User has tutor_eligible set to 0 -- not able to tutor any class
                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                        return new StartTutorSessionResponseItem();
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new StartTutorSessionResponseItem();
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
                    // Make sure tutor is eligible to tutor
                    if (checkTutorEligibility(item.userEmail))
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
                                    // Remove pairing from tutor_sessions_active table
                                    command.CommandText = "DELETE FROM tutor_sessions_active WHERE tutorEmail = ?tutorEmail";

                                    if (command.ExecuteNonQuery() >= 0)
                                    {
                                        // Calculate the total cost of the tutoring session
                                        TimeSpan diff = sessionEndTime.Subtract(returnedSessionStartTime);
                                        double cost = diff.TotalMinutes * 0.25;
                                        cost = Math.Round(cost, 2);

                                        // Insert pairing into the tutor_sesssions_complete table
                                        command.CommandText = "INSERT INTO tutor_sessions_completed (studentEmail, tutorEmail, course, session_start_time, session_end_time, session_cost) VALUES (?studentEmail, ?tutorEmail, ?course, ?session_start_time, ?session_end_time, ?session_cost)";
                                        command.Parameters.AddWithValue("studentEmail", returnedStudentEmail);
                                        command.Parameters.AddWithValue("course", returnedCourseName);
                                        command.Parameters.AddWithValue("session_start_time", returnedSessionStartTime);
                                        command.Parameters.AddWithValue("session_end_time", sessionEndTime);
                                        command.Parameters.AddWithValue("session_cost", cost);

                                        if (command.ExecuteNonQuery() > 0)
                                        {
                                            int returnedTutorSessionID = -1;

                                            // Get the tutor_session_id
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
                                                // Return the completed tutor session information
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
                                                // Getting the tutor_session_id from the tutor_sessions_completed table failed
                                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.ExpectationFailed;
                                                return new EndTutorSessionResponseItem();
                                            }
                                        }
                                        else
                                        {
                                            // Inserting into tutor_sessions_completed failed
                                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadRequest;
                                            return new EndTutorSessionResponseItem();
                                        }
                                    }
                                    else
                                    {
                                        // Deleting from tutor_sessions_active failed
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                        return new EndTutorSessionResponseItem();
                                    }
                                }
                                else
                                {
                                    // Could not find the pairing in the tutor_sessions_active table
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
                        // User has tutor_eligible set to 0-- not able to tutor any class
                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                        return new EndTutorSessionResponseItem();
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new EndTutorSessionResponseItem();
                }
            }
        }

        public UpdateStudentLocationResponseItem UpdateStudentLocation(UpdateStudentLocationRequestItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();

                            // Insert student's new location into the tutor_sessions_pairing table
                            command.CommandText = "UPDATE tutor_sessions_pairing SET studentLatitude = ?studentLatitude, studentLongitude = ?studentLongitude WHERE studentEmail = ?studentEmail";
                            command.Parameters.AddWithValue("studentLatitude", item.latitude);
                            command.Parameters.AddWithValue("studentLongitude", item.longitude);
                            command.Parameters.AddWithValue("studentEmail", item.userEmail);

                            if (command.ExecuteNonQuery() > 0)
                            {
                                // Retrieve the tutor's location to send back to the student
                                command.CommandText = "SELECT tutorEmail, tutorLatitude, tutorLongitude FROM tutor_sessions_pairing WHERE studentEmail = ?studentEmail";

                                UpdateStudentLocationResponseItem locationResponse = new UpdateStudentLocationResponseItem();

                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        locationResponse.tutorEmail = reader.GetString("tutorEmail");
                                        locationResponse.tutorLatitude = reader.GetString("tutorLatitude");
                                        locationResponse.tutorLongitude = reader.GetString("tutorLongitude");
                                    }
                                }

                                return locationResponse;
                            }
                            else
                            {
                                // Updating the student's location in the tutor_sessions_pairing table failed
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                                return new UpdateStudentLocationResponseItem();
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
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new UpdateStudentLocationResponseItem();
                }
            }
        }

        public UpdateTutorLocationResponseItem UpdateTutorLocation(UpdateTutorLocationRequestItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Make sure tutor is eligible to tutor
                    if (checkTutorEligibility(item.userEmail))
                    {
                        using (MySqlConnection conn = new MySqlConnection(connectionString))
                        {
                            try
                            {
                                conn.Open();

                                MySqlCommand command = conn.CreateCommand();

                                // Insert tutors's new location into the tutor_sessions_pairing table
                                command.CommandText = "UPDATE tutor_sessions_pairing SET tutorLatitude = ?tutorLatitude, tutorLongitude = ?tutorLongitude WHERE tutorEmail = ?tutorEmail";
                                command.Parameters.AddWithValue("tutorLatitude", item.latitude);
                                command.Parameters.AddWithValue("tutorLongitude", item.longitude);
                                command.Parameters.AddWithValue("tutorEmail", item.userEmail);

                                if (command.ExecuteNonQuery() > 0)
                                {
                                    // Retrieve the student's location to send back to the tutor
                                    command.CommandText = "SELECT studentEmail, studentLatitude, studentLongitude FROM tutor_sessions_pairing WHERE tutorEmail = ?tutorEmail";

                                    UpdateTutorLocationResponseItem locationResponse = new UpdateTutorLocationResponseItem();

                                    using (MySqlDataReader reader = command.ExecuteReader())
                                    {
                                        while (reader.Read())
                                        {
                                            locationResponse.studentEmail = reader.GetString("studentEmail");
                                            locationResponse.studentLatitude = reader.GetString("studentLatitude");
                                            locationResponse.studentLongitude = reader.GetString("studentLongitude");
                                        }
                                    }

                                    return locationResponse;
                                }
                                else
                                {
                                    // Updating the tutor's location in the tutor_sessions_pairing table failed
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                                    return new UpdateTutorLocationResponseItem();
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
                        // User has tutor_eligible set to 0 -- not able to tutor any class
                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                        return new UpdateTutorLocationResponseItem();
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new UpdateTutorLocationResponseItem();
                }
            }
        }

        public RateTutorResponseItem RateTutor(RateTutorItem item)
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

                            MySqlCommand command = conn.CreateCommand();

                            // Check to make sure the student hasn't already rated the tutor
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
                                // Check to see if student & tutor were involved in the specified session ID
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
                                    // Add the student's raiting of the stutor into the tutor_ratings table
                                    command.CommandText = "INSERT INTO tutor_ratings VALUES (?tutorSessionID, ?tutorEmail, ?studentEmail, ?rating)";
                                    command.Parameters.AddWithValue("studentEmail", returnedStudentEmail);
                                    command.Parameters.AddWithValue("tutorEmail", returnedTutorEmail);
                                    command.Parameters.AddWithValue("rating", item.rating);

                                    if (command.ExecuteNonQuery() > 0)
                                    {
                                        // Rating added successfully
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                        return new RateTutorResponseItem();
                                    }
                                    else
                                    {
                                        // Insert rating into tutor_ratings table failed
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.ExpectationFailed;
                                        return new RateTutorResponseItem();
                                    }
                                }
                                else
                                {
                                    // Student & tutor were not apart of the same tutor session
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                    return new RateTutorResponseItem();
                                }
                            }
                            else
                            {
                                // There is already a record in the tutor_ratings table for this session ID
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.NotAcceptable;
                                return new RateTutorResponseItem();
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
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new RateTutorResponseItem();
                }
            }
        }

        public RateStudentResponseItem RateStudent(RateStudentItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Make sure tutor is eligible to tutor
                    if (checkTutorEligibility(item.userEmail))
                    {
                        String returnedStudentEmail = "";
                        String returnedTutorEmail = "";

                        using (MySqlConnection conn = new MySqlConnection(connectionString))
                        {
                            try
                            {
                                conn.Open();

                                MySqlCommand command = conn.CreateCommand();

                                // Check to make sure the tutor hasn't already rated the student
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
                                    // Check to see if student & tutor were involved in the specified session ID
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
                                        // Add the tutor's raiting of the student into the tutor_ratings table
                                        command.CommandText = "INSERT INTO student_ratings VALUES (?tutorSessionID, ?tutorEmail, ?studentEmail, ?rating)";
                                        command.Parameters.AddWithValue("studentEmail", returnedStudentEmail);
                                        command.Parameters.AddWithValue("tutorEmail", returnedTutorEmail);
                                        command.Parameters.AddWithValue("rating", item.rating);

                                        if (command.ExecuteNonQuery() > 0)
                                        {
                                            // Rating added successfully
                                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                            return new RateStudentResponseItem();
                                        }
                                        else
                                        {
                                            // Insert rating into tutor_ratings table failed
                                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.ExpectationFailed;
                                            return new RateStudentResponseItem();
                                        }
                                    }
                                    else
                                    {
                                        // Student & tutor were not apart of the same tutor session
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                        return new RateStudentResponseItem();
                                    }
                                }
                                else
                                {
                                    // There is already a record in the tutor_ratings table for  this session ID
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.NotAcceptable;
                                    return new RateStudentResponseItem();
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
                        // User has tutor_eligible set to 0 -- not able to tutor any class
                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                        return new RateStudentResponseItem();
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new RateStudentResponseItem();
                }
            }
        }

        public CreateStudyHotspotResponseItem CreateStudyHotspot(CreateStudyHotspotRequestItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Check that the student is in the specified course
                    if (verifyStudentInCourse(item.userEmail, item.course))
                    {
                        String returnedHotspotID = "";

                        using (MySqlConnection conn = new MySqlConnection(connectionString))
                        {
                            try
                            {
                                conn.Open();

                                MySqlCommand command = conn.CreateCommand();
                                
                                // Insert the hotspot into the study_hotspots table
                                command.CommandText = "INSERT INTO study_hotspots (owner_email, course_name, latitude, longitude, student_count) VALUES (?owner_email, ?course_name, ?latitude, ?longitude, 1)";
                                command.Parameters.AddWithValue("owner_email", item.userEmail);
                                command.Parameters.AddWithValue("course_name", item.course);
                                command.Parameters.AddWithValue("latitude", item.latitude);
                                command.Parameters.AddWithValue("longitude", item.longitude);

                                if (command.ExecuteNonQuery() > 0)
                                {
                                    // Retreive the new hotspot_id
                                    command.CommandText = "SELECT hotspot_id FROM study_hotspots WHERE owner_email = ?owner_email";
                                    using (MySqlDataReader reader = command.ExecuteReader())
                                    {
                                        while (reader.Read())
                                        {
                                            returnedHotspotID = reader.GetString("hotspot_id");
                                        }
                                    }

                                    // Insert creator of hotspot into the hotspots_members table
                                    command.CommandText = "INSERT INTO study_hotspots_members (hotspot_id, email) VALUES (?hotspot_id, ?email)";
                                    command.Parameters.AddWithValue("email", item.userEmail);
                                    command.Parameters.AddWithValue("hotspot_id", returnedHotspotID);

                                    if (command.ExecuteNonQuery() > 0)
                                    {
                                        // Hotspot created successfully
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                        return new CreateStudyHotspotResponseItem();
                                    }
                                    else
                                    {
                                        // Creator assigned to hotspot in hotspots_members table failed
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                        return new CreateStudyHotspotResponseItem();
                                    }
                                }
                                else
                                {
                                    // Insertion of hotspot into study_hotspots table failed
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                    return new CreateStudyHotspotResponseItem();
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
                        // Student is not in the specified course
                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                        return new CreateStudyHotspotResponseItem();
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new CreateStudyHotspotResponseItem();
                }
            }
        }

        public FindStudyHotspotReturnItem FindStudyHotspots(StudyHotspotItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Check that the student is in the specified course
                    if (verifyStudentInCourse(item.userEmail, item.course))
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

                                // Find all the hotspots associated with the course name provided
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

                                        // Make sure student is less than 5 miles from the hotspot before adding it to the return list
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

                        // Return study hotspots
                        FindStudyHotspotReturnItem studyHotspotsList = new FindStudyHotspotReturnItem();
                        studyHotspotsList.studyHotspots = availableHotspots;
                        return studyHotspotsList;
                    }
                    else
                    {
                        // Student is not in the specified course
                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                        return new FindStudyHotspotReturnItem();
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new FindStudyHotspotReturnItem();
                }
            }
        }

        public StudyHotspotJoinResponseItem JoinStudyHotspot(StudyHotspotJoinItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Check that the student is in the specified course
                    if (verifyStudentInCourse(item.userEmail, item.course))
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
                                            // Adding user to study hotspot successful 
                                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                            return new StudyHotspotJoinResponseItem();
                                        }
                                        else
                                        {
                                            // Updating student count failed
                                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                            return new StudyHotspotJoinResponseItem();
                                        }
                                    }
                                    else
                                    {
                                        // Inserting user into study_hotspots_members table failed
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                        return new StudyHotspotJoinResponseItem();
                                    }
                                }
                                else
                                {
                                    // Study hotspot no longer exists
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Gone;
                                    return new StudyHotspotJoinResponseItem();
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
                        // Student is not in the specified course
                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                        return new StudyHotspotJoinResponseItem();
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new StudyHotspotJoinResponseItem();
                }
            }
        }

        public StudyHotspotLeaveRequestItem LeaveStudyHotspot(StudyHotspotLeaveItem item)
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

                            // Delete user from the study hotspot
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
                                    // Deleting user from study hotspot successful
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                    return new StudyHotspotLeaveRequestItem();
                                }
                                else
                                {
                                    // Updating study hotspot count failed
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                    return new StudyHotspotLeaveRequestItem();
                                }
                            }
                            else
                            {
                                // Deleting user from study_hotspots_members table failed
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Gone;
                                return new StudyHotspotLeaveRequestItem();
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
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new StudyHotspotLeaveRequestItem();
                }
            }
        }

        public StudyHotspotResponseItem GetStudyHotspotMembers(StudyHotspotGetMemberItem item)
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

                            // Get emails of all the members in the study hotspot
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
                                // Get first and last name of all members in the hotspot
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

                    // Return study hotspot members' names
                    StudyHotspotResponseItem members = new StudyHotspotResponseItem();
                    members.hotspotMembers = hotspotMembers;
                    return members;
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new StudyHotspotResponseItem();
                }
            }
        }

        public StudyHotspotDeleteResponseItem DeleteStudyHotspot(StudyHotspotDeleteItem item)
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
                                    // Deleting the study hotspot was successful
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                    return new StudyHotspotDeleteResponseItem();
                                }
                                else
                                {
                                    // Deleting the study hotspot failed
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                    return new StudyHotspotDeleteResponseItem();
                                }
                            }
                            else
                            {
                                // User trying to delete the study hotspot does not own the study hotspot
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                                return new StudyHotspotDeleteResponseItem();
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
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new StudyHotspotDeleteResponseItem();
                }
            }
        }

        public ScheduleTutorResponseItem ScheduleTutor(ScheduleTutorItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Check that the student is in the specified course
                    if (verifyStudentInCourse(item.userEmail, item.course))
                    {
                        // Store student's tutor request in DB
                        using (MySqlConnection conn = new MySqlConnection(connectionString))
                        {
                            try
                            {
                                conn.Open();

                                MySqlCommand command = conn.CreateCommand();
                                command.CommandText = "INSERT INTO tutor_requests VALUES (?studentEmail, ?course, ?topic, ?dateTime, ?duration)";
                                command.Parameters.AddWithValue("studentEmail", item.userEmail);
                                command.Parameters.AddWithValue("course", item.course);
                                command.Parameters.AddWithValue("topic", item.topic);
                                command.Parameters.AddWithValue("dateTime", item.dateTime);
                                command.Parameters.AddWithValue("duration", item.duration);

                                if (command.ExecuteNonQuery() > 0)
                                {
                                    // Student's request stored successfully
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                    return new ScheduleTutorResponseItem();
                                }
                                else
                                {
                                    // Student's request failed
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadRequest;
                                    return new ScheduleTutorResponseItem();
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
                        // Student is not in the specified course
                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                        return new ScheduleTutorResponseItem();
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new ScheduleTutorResponseItem();
                }
            }
        }

        public FindAllScheduleTutorResponseItem FindAllScheduleTutorRequests(FindAllScheduleTutorRequestItem item)
        {
            lock (this)
            {

                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Make sure tutor is eligible to tutor
                    if (checkTutorEligibility(item.userEmail))
                    {
                        List<ScheduleTutorRequestItem> studentRequests = new List<ScheduleTutorRequestItem>();

                        using (MySqlConnection conn = new MySqlConnection(connectionString))
                        {
                            try
                            {
                                conn.Open();

                                MySqlCommand command = conn.CreateCommand();

                                // Retrieve all tutor requests for the specified course
                                command.CommandText = "SELECT * FROM tutor_requests WHERE course = ?courseName";
                                command.Parameters.AddWithValue("courseName", item.course);

                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        ScheduleTutorRequestItem request = new ScheduleTutorRequestItem();
                                        request.studentEmail = reader.GetString("student_email");
                                        request.course = reader.GetString("course");
                                        request.topic = reader.GetString("topic");
                                        request.dateTime = reader.GetString("date_time");
                                        request.duration = reader.GetString("duration");

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

                        // Return the requests to the  tutor
                        FindAllScheduleTutorResponseItem studentRequestItemList = new FindAllScheduleTutorResponseItem();
                        studentRequestItemList.tutorRequestItems = studentRequests;
                        return studentRequestItemList;
                    }
                    else
                    {
                        // User has tutor_eligible set to 0 -- not able to tutor any class
                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                        return new FindAllScheduleTutorResponseItem();
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new FindAllScheduleTutorResponseItem();
                }
            }
        }

        public FindAllScheduleTutorAcceptedResponsetItem FindAllScheduleTutorAcceptedRequests(FindAllScheduleTutorAcceptedRequestItem item)
        {
            lock (this)
            {

                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Make sure tutor is eligible to tutor
                    if (checkTutorEligibility(item.userEmail))
                    {
                        List<FindAllScheduleTutorAcceptedItem> studentRequests = new List<FindAllScheduleTutorAcceptedItem>();

                        using (MySqlConnection conn = new MySqlConnection(connectionString))
                        {
                            try
                            {
                                conn.Open();

                                MySqlCommand command = conn.CreateCommand();

                                // Retrieve all tutor requests for the specified course
                                command.CommandText = "SELECT * FROM tutor_requests_accepted WHERE course = ?courseName";
                                command.Parameters.AddWithValue("courseName", item.course);

                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        FindAllScheduleTutorAcceptedItem request = new FindAllScheduleTutorAcceptedItem();
                                        request.studentEmail = reader.GetString("student_email");
                                        request.course = reader.GetString("course");
                                        request.topic = reader.GetString("topic");
                                        request.dateTime = reader.GetString("date_time");
                                        request.duration = reader.GetString("duration");

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

                        // Return the requests to the  tutor
                        FindAllScheduleTutorAcceptedResponsetItem studentRequestItemList = new FindAllScheduleTutorAcceptedResponsetItem();
                        studentRequestItemList.tutorRequestItems = studentRequests;
                        return studentRequestItemList;
                    }
                    else
                    {
                        // User has tutor_eligible set to 0 -- not able to tutor any class
                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                        return new FindAllScheduleTutorAcceptedResponsetItem();
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new FindAllScheduleTutorAcceptedResponsetItem();
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
                    // Make sure tutor is eligible to tutor
                    if (checkTutorEligibility(item.userEmail))
                    {
                        String returnedStudentEmail = "";
                        String returnedCourseName = "";
                        String returnedTopic = "";
                        String returnedDateTime = "";
                        String returnedDuration = "";

                        using (MySqlConnection conn = new MySqlConnection(connectionString))
                        {
                            try
                            {
                                conn.Open();

                                MySqlCommand command = conn.CreateCommand();
                                // Get selected student request information 
                                command.CommandText = "SELECT student_email, course, topic, DATE_FORMAT(date_time, '%Y-%m-%d %T') as date_time, duration FROM tutor_requests WHERE student_email = ?studentEmail AND course = ?course";
                                command.Parameters.AddWithValue("studentEmail", item.studentEmail);
                                command.Parameters.AddWithValue("course", item.course);

                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        returnedStudentEmail = reader.GetString("student_email");
                                        returnedCourseName = reader.GetString("course");
                                        returnedTopic = reader.GetString("topic");
                                        returnedDateTime = reader.GetString("date_time");
                                        returnedDuration = reader.GetString("duration");
                                    }
                                }

                                if (returnedStudentEmail == item.studentEmail && returnedCourseName == item.course)
                                {
                                    // Remove scheduled tutor request from the tutor_requests table
                                    command.CommandText = "DELETE FROM tutor_requests WHERE student_email = ?studentEmail AND course = ?course";

                                    if (command.ExecuteNonQuery() >= 0)
                                    {
                                        // Insert the pairing into the tutor_requests_accepted table
                                        command.CommandText = "INSERT INTO tutor_requests_accepted VALUES (?student_email, ?tutor_email, ?course, ?topic, ?dateTime, ?duration)";
                                        command.Parameters.Clear();
                                        command.Parameters.AddWithValue("student_email", item.studentEmail);
                                        command.Parameters.AddWithValue("tutor_email", item.userEmail);
                                        command.Parameters.AddWithValue("course", returnedCourseName);
                                        command.Parameters.AddWithValue("topic", returnedTopic);
                                        command.Parameters.AddWithValue("dateTime", returnedDateTime);
                                        command.Parameters.AddWithValue("duration", returnedDuration);

                                        if (command.ExecuteNonQuery() > 0)
                                        {
                                            // Pairing of the student and tutor scheduled request was successful
                                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                            AcceptStudentScheduleRequestResponseItem paired = new AcceptStudentScheduleRequestResponseItem();
                                            paired.student_email = item.studentEmail;
                                            paired.tutor_email = item.userEmail;
                                            paired.course = returnedCourseName;
                                            paired.topic = returnedTopic;
                                            paired.dateTime = returnedDateTime;
                                            paired.duration = returnedDuration;

                                            return paired;
                                        }
                                        else
                                        {
                                            // Insert pairing into tutor_requests_accepted table failed
                                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadRequest;
                                            return new AcceptStudentScheduleRequestResponseItem();
                                        }
                                    }
                                    else
                                    {
                                        // Deleting from tutor_requests table failed
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                        return new AcceptStudentScheduleRequestResponseItem();
                                    }
                                }
                                else
                                {
                                    // Student schedule request no longer available
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
                        // User has tutor_eligible set to 0-- not able to tutor any class
                       WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                       return new AcceptStudentScheduleRequestResponseItem();
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new AcceptStudentScheduleRequestResponseItem();
                }
            }
        }

        public CheckPairedStatusResponseItem CheckScheduledPairedStatus(CheckPairedStatusItem item)
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
                            command.CommandText = "SELECT student_email, course, topic, DATE_FORMAT(date_time, '%Y-%m-%d %T') as date_time, duration FROM tutor_requests WHERE student_email = ?userEmail";
                            command.Parameters.AddWithValue("userEmail", item.userEmail);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    PairedScheduledStatusItem statusItem = new PairedScheduledStatusItem();
                                    statusItem.studentEmail = reader.GetString("student_email");
                                    statusItem.course = reader.GetString("course");
                                    statusItem.topic = reader.GetString("topic");
                                    statusItem.dateTime = reader.GetString("date_time");
                                    statusItem.duration = reader.GetString("duration");
                                    statusItem.isPaired = false;
                                    listings.Add(statusItem);
                                }
                            }

                            // Check tutor_requests_accepted table for accepted requests
                            command.CommandText = "SELECT student_email, tutor_email, course, topic, DATE_FORMAT(date_time, '%Y-%m-%d %T') as date_time, duration FROM tutor_requests_accepted WHERE student_email = ?userEmail";

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    PairedScheduledStatusItem statusItem = new PairedScheduledStatusItem();
                                    statusItem.studentEmail = reader.GetString("student_email");
                                    statusItem.tutorEmail = reader.GetString("tutor_email");
                                    statusItem.course = reader.GetString("course");
                                    statusItem.topic = reader.GetString("topic");
                                    statusItem.dateTime = reader.GetString("date_time");
                                    statusItem.duration = reader.GetString("duration");
                                    statusItem.isPaired = true;
                                    listings.Add(statusItem);
                                }
                            }

                            // Return all schedule tutor requests for the student
                            CheckPairedStatusResponseItem requests = new CheckPairedStatusResponseItem();
                            requests.requests = listings;
                            return requests;
                        }
                        catch (Exception e)
                        {
                            throw e;
                        }
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new CheckPairedStatusResponseItem();
                }
            }
        }

        public StartScheduledTutorSessionResponseItem StartScheduledTutorSession(StartScheduledTutorSessionItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Make sure tutor is eligible to tutor
                    if (checkTutorEligibility(item.userEmail))
                    {
                        // Get info from tutor_sessions_pairing table
                        String returnedStudentEmail = "";
                        String returnedTutorEmail = "";
                        String returnedCourseName = "";
                        String returnedTopic = "";
                        String returnedDuration = "";

                        using (MySqlConnection conn = new MySqlConnection(connectionString))
                        {
                            try
                            {
                                conn.Open();

                                MySqlCommand command = conn.CreateCommand();

                                // Get information from tutor_requests_accepted table
                                command.CommandText = "SELECT student_email, tutor_email, course, topic, duration FROM tutor_requests_accepted WHERE tutor_email = ?tutorEmail  AND course = ?course AND date_time = ?dateTime";
                                command.Parameters.AddWithValue("tutorEmail", item.userEmail);
                                command.Parameters.AddWithValue("course", item.course);
                                command.Parameters.AddWithValue("dateTime", item.dateTime);

                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        returnedStudentEmail = reader.GetString("student_email");
                                        returnedTutorEmail = reader.GetString("tutor_email");
                                        returnedCourseName = reader.GetString("course");
                                        returnedTopic = reader.GetString("topic");
                                        returnedDuration = reader.GetString("duration");
                                    }
                                }

                                //TODO: Add check to make sure session date is the same as the current date

                                if (returnedTutorEmail == item.userEmail && returnedCourseName == item.course)
                                {
                                    // Remove pairing from tutor_requests_accepted table
                                    command.CommandText = "DELETE FROM tutor_requests_accepted WHERE tutor_email = ?tutorEmail AND course = ?course AND date_time = ?dateTime";

                                    if (command.ExecuteNonQuery() >= 0)
                                    {
                                        // Insert pairing into the tutor_sesssions_active table
                                        command.CommandText = "INSERT INTO tutor_sessions_active VALUES (?studentEmail, ?tutorEmail, ?course, ?session_start_time)";
                                        command.Parameters.AddWithValue("studentEmail", returnedStudentEmail);
                                        command.Parameters.AddWithValue("session_start_time", DateTime.Now);

                                        if (command.ExecuteNonQuery() > 0)
                                        {
                                            // Tutor session started successfully
                                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                            return new StartScheduledTutorSessionResponseItem();
                                        }
                                        else
                                        {
                                            // Insert into tutor_sessions_active table failed
                                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadRequest;
                                            return new StartScheduledTutorSessionResponseItem();
                                        }
                                    }
                                    else
                                    {
                                        // Deleting from tutor_requests_accepted table failed
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Conflict;
                                        return new StartScheduledTutorSessionResponseItem();
                                    }
                                }
                                else
                                {
                                    // Pairing is no longer active
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Gone;
                                    return new StartScheduledTutorSessionResponseItem();
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
                        // User has tutor_eligible set to 0-- not able to tutor any class
                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Forbidden;
                        return new StartScheduledTutorSessionResponseItem();
                    }
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new StartScheduledTutorSessionResponseItem();
                }
            }
        }

        public ReportTutorGetTutorListResponseItem ReportTutorGetTutorList(ReportTutorGetTutorListRequestItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    String returnedEmail = "";
                    String returnedFirstName = "";
                    String returnedLastName = "";

                    List<String> tutorEmails = new List<String>();

                    List<ReportTutorGetTutorListItem> tutorResponseItems = new List<ReportTutorGetTutorListItem>();

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();

                            // Get all tutor's that the student has met with
                            command.CommandText = "SELECT tutorEmail FROM tutor_sessions_completed WHERE studentEmail = ?studentEmail";
                            command.Parameters.AddWithValue("studentEmail", item.userEmail);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    returnedEmail = reader.GetString("tutorEmail");
                                    if (tutorEmails.Contains(returnedEmail))
                                    {
                                        continue;
                                    }
                                    else
                                    {
                                        tutorEmails.Add(returnedEmail);
                                    }
                                }
                            }

                            for (int i = 0; i < tutorEmails.Count; i++)
                            {
                                // Get the tutors' first and last names
                                command.CommandText = "SELECT first_name, last_name FROM users WHERE email = ?email";
                                command.Parameters.Clear();
                                command.Parameters.AddWithValue("email", tutorEmails[i]);

                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        returnedFirstName = reader.GetString("first_name");
                                        returnedLastName = reader.GetString("last_name");
                                    }
                                }

                                ReportTutorGetTutorListItem tutor = new ReportTutorGetTutorListItem();
                                tutor.tutorEmail = tutorEmails[i];
                                tutor.tutorFirstName = returnedFirstName;
                                tutor.tutorLastName = returnedLastName;

                                tutorResponseItems.Add(tutor);
                            }
                        }
                        catch (Exception e)
                        {
                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.ServiceUnavailable;
                            throw e;
                        }
                    }


                    // Return the list of tutors that the student met with
                    ReportTutorGetTutorListResponseItem responseList = new ReportTutorGetTutorListResponseItem();
                    responseList.tutorList = tutorResponseItems;
                    return responseList;
                }
                else
                {
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new ReportTutorGetTutorListResponseItem();
                }
            }
        }

        public ReportTutorGetSessionListResponseItem ReportTutorGetSessionList(ReportTutorGetSessionListRequestItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    List<ReportTutorGetSessionListItem> tutorResponseItems = new List<ReportTutorGetSessionListItem>();

                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();

                            // Get all tutoring sessions the student had with the specified tutor
                            command.CommandText = "SELECT tutor_session_id, course, session_start_time, session_end_time, session_cost FROM tutor_sessions_completed WHERE studentEmail = ?studentEmail AND tutorEmail = ?tutorEmail";
                            command.Parameters.AddWithValue("studentEmail", item.userEmail);
                            command.Parameters.AddWithValue("tutorEmail", item.tutorEmail);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    ReportTutorGetSessionListItem sessionItem = new ReportTutorGetSessionListItem();
                                    sessionItem.tutorEmail = item.tutorEmail;
                                    sessionItem.tutorFirstName = item.tutorFirstName;
                                    sessionItem.tutorLastName = item.tutorLastName;
                                    sessionItem.tutorSessionID = reader.GetString("tutor_session_id");
                                    sessionItem.course = reader.GetString("course");
                                    sessionItem.sessionStartTime = reader.GetString("session_start_time");
                                    sessionItem.sessionEndTime = reader.GetString("session_end_time");
                                    sessionItem.sessionCost = reader.GetString("session_cost");

                                    tutorResponseItems.Add(sessionItem);
                                }
                            }

                            // Return the list of tutoring sessions
                            ReportTutorGetSessionListResponseItem responseItem = new ReportTutorGetSessionListResponseItem();
                            responseItem.tutorList = tutorResponseItems;
                            return responseItem;
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
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new ReportTutorGetSessionListResponseItem();
                }
            }
        }

        public ReportTutorResponseItem ReportTutor(ReportTutorRequestItem item)
        {
            lock (this)
            {
                // Check that the user token is valid
                if (checkUserToken(item.userEmail, item.userToken))
                {
                    // Insert report into reported_tutor table
                    using (MySqlConnection conn = new MySqlConnection(connectionString))
                    {
                        try
                        {
                            conn.Open();

                            MySqlCommand command = conn.CreateCommand();

                            // Store user report in reported_tutors table
                            command.CommandText = "INSERT INTO reported_tutors VALUES (?tutorSessionID, ?studentEmail, ?tutorEmail, ?message, ?reportDate)";
                            command.Parameters.AddWithValue("tutorSessionID", item.tutorSessionID);
                            command.Parameters.AddWithValue("studentEmail", item.userEmail);
                            command.Parameters.AddWithValue("tutorEmail", item.tutorEmail);
                            command.Parameters.AddWithValue("message", item.message);
                            command.Parameters.AddWithValue("reportDate", DateTime.Now);

                            if (command.ExecuteNonQuery() > 0)
                            {
                                // See if tutor now has 5 reports, if so, deactivate tutor status
                                int reportCount = -1;

                                command.CommandText = "SELECT count(*) as count FROM reported_tutors WHERE tutorEmail = ?tutorEmail";
                                using (MySqlDataReader reader = command.ExecuteReader())
                                {
                                    while (reader.Read())
                                    {
                                        reportCount = reader.GetInt32("count");
                                    }
                                }

                                if (reportCount >= 5)
                                {
                                    command.CommandText = "UPDATE users SET tutor_eligible = ?eligibleFlag WHERE email = ?tutorEmail";
                                    command.Parameters.AddWithValue("eligibleFlag", 0);

                                    if (command.ExecuteNonQuery() > 0)
                                    {
                                        // Reporting tutor & deactivating tutor succeeded
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                        return new ReportTutorResponseItem();
                                    }
                                    else
                                    {
                                        // Reporting tutor succeeded, but deactivating tutor failed
                                        WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.NotModified;
                                        return new ReportTutorResponseItem();
                                    }
                                }
                                else
                                {
                                    // Reporting tutor & no deactivating tutor succeeded
                                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                                    return new ReportTutorResponseItem();
                                }
                            }
                            else
                            {
                                // Inserting student's report for tutor failed
                                WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.BadRequest;
                                return new ReportTutorResponseItem();
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
                    // User's email & token combo is not valid
                    WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                    return new ReportTutorResponseItem();
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

        private Boolean checkTutorEligibility(String userEmail)
        {
            int returnedTutorEligible = -1;
            using (MySqlConnection conn = new MySqlConnection(connectionString))
            {
                try
                {
                    conn.Open();

                    MySqlCommand command = conn.CreateCommand();

                    command.CommandText = "SELECT tutor_eligible FROM users WHERE email = ?userEmail";
                    command.Parameters.AddWithValue("userEmail", userEmail);

                    using (MySqlDataReader reader = command.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            returnedTutorEligible = reader.GetInt32("tutor_eligible");
                        }
                    }

                    if (returnedTutorEligible == 1)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                catch (Exception e)
                {
                    throw e;
                }
            }
        }

        private Boolean verifyStudentInCourse(String userEmail, String courseName)
        {
            String returnedStudentEmail = "";
            String returnedCourseName = "";

            using (MySqlConnection conn = new MySqlConnection(connectionString))
            {
                try
                {
                    conn.Open();

                    MySqlCommand command = conn.CreateCommand();

                    command.CommandText = "SELECT email, name FROM student_courses WHERE email = ?userEmail and name = ?courseName";
                    command.Parameters.AddWithValue("userEmail", userEmail);
                    command.Parameters.AddWithValue("courseName", courseName);

                    using (MySqlDataReader reader = command.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            returnedStudentEmail = reader.GetString("email");
                            returnedCourseName = reader.GetString("name");
                        }
                    }

                    if (returnedStudentEmail == userEmail && returnedCourseName == courseName)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                catch (Exception e)
                {
                    throw e;
                }
            }
        }

      
    }
}
