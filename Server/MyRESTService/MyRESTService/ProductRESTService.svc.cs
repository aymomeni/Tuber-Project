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

namespace ToDoList
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the class name "ProductRESTService" in code, svc and config file together.
    // NOTE: In order to launch WCF Test Client for testing this service, please select ProductRESTService.svc or ProductRESTService.svc.cs at the Solution Explorer and start debugging.
    public class ProductRESTService : IToDoService
    {
        //public const string connectionString = "Server=maria.eng.utah.edu;Database=tuber;UID=tobin;Password=traflip53";
        public const string connectionString = "Server=sql3.freemysqlhosting.net;Database=sql3153117;UID=sql3153117;Password=vjbaNtDruW;";
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
        public UserItem VerifyUser(UserItem data)
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
                            //UserItem user = new UserItem();
                            //user.userEmail = null;
                            //user.userPassword = null;
                            //user.userCourses = new ArrayList();

                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.Unauthorized;
                            return new UserItem();
                        }
                        else
                        {
                            ArrayList courses = new ArrayList();
                            command.CommandText = "select name from courses where email = ?email";
                            command.Parameters.AddWithValue("email", returnedUserEmail);

                            using (MySqlDataReader reader = command.ExecuteReader())
                            {
                                while (reader.Read())
                                {
                                    courses.Add(reader.GetString("name"));
                                }
                            }

                            UserItem user = new UserItem();
                            user.userEmail = returnedUserEmail;
                            user.userPassword = returnedUserPassword;
                            user.userCourses = courses;

                            WebOperationContext.Current.OutgoingResponse.StatusCode = HttpStatusCode.OK;
                            return user;
           
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
}
