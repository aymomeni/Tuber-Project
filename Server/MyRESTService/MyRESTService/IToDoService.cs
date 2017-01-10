using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Collections;
using System.Text;

namespace ToDoList
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the interface name "IProductRESTService" in both code and config file together.
    [ServiceContract]
    public interface IToDoService
    {
        [OperationContract]
        [WebInvoke(Method = "GET", ResponseFormat = WebMessageFormat.Xml,
                                   BodyStyle = WebMessageBodyStyle.Bare,
                                   UriTemplate = "GetProductList/")]
        List<Product> GetProductList();

        /// <summary>
        /// Accepts HTTP request to create new users in the databse.
        /// </summary>
        /// <param name="data"></param>
        /// <returns></returns>
        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/makeuser",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        MakeUserItem MakeUser(UserItem data);

        /// <summary>
        /// Accepts HTTP request to verify user credentials received match what is in the database 
        /// to allow the user to login to the platform. 
        /// </summary>
        /// <param name="data"></param>
        /// <returns></returns>
        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/verifyuser",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        UserItem VerifyUser(UserItem data);

        /// <summary>
        /// Accepts HTTP request to make a tutor available.
        /// </summary>
        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/maketutoravailable",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        void MakeTutorAvailable(TutorUserItem data);
    }
}

