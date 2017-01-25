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
        //[OperationContract]
        //[WebInvoke(Method = "POST",
        //    UriTemplate = "/makeuser",
        //    RequestFormat = WebMessageFormat.Json,
        //    ResponseFormat = WebMessageFormat.Json,
        //    BodyStyle = WebMessageBodyStyle.Bare)]
        //MakeUserItem MakeUser(UserItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/createuser",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        MakeUserItem CreateUser(CreateUserItem data);

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
        VerifiedUserItem VerifyUser(UserItem data);

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

        //[OperationContract]
        //[WebInvoke(Method = "DELETE",
        //    UriTemplate = "/deletetutoravailable/{userEmail}",
        //    RequestFormat = WebMessageFormat.Json,
        //    ResponseFormat = WebMessageFormat.Json,
        //    BodyStyle = WebMessageBodyStyle.Bare)]
        //void DeleteTutorAvailable(string userEmail);

        [OperationContract]
        [WebInvoke(Method = "POST",
        UriTemplate = "/deletetutoravailable",
        RequestFormat = WebMessageFormat.Json,
        ResponseFormat = WebMessageFormat.Json,
        BodyStyle = WebMessageBodyStyle.Bare)]
        void DeleteTutorAvailable(DeleteTutorUserItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
           UriTemplate = "/findavailabletutors",
           RequestFormat = WebMessageFormat.Json,
           ResponseFormat = WebMessageFormat.Json,
           BodyStyle = WebMessageBodyStyle.Bare)]
        List<AvailableTutorUserItem> FindAvailableTutors(TutorUserItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/pairstudenttutor",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        StudentTutorPairedItem PairStudentTutor(StudentTutorRequestItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/createstudyhotspot",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        void CreateStudyHotspot(CreateStudyHotspotRequestItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/findstudyhotspots",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        List<AvailableStudyHotspotItem> FindStudyHotspots(StudyHotspotItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/joinstudyhotspot",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        void JoinStudyHotspot(StudyHotspotJoinItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/leavestudyhotspot",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        void LeaveStudyHotspot(StudyHotspotLeaveItem data);

        [OperationContract]
        [WebInvoke(Method = "POST",
            UriTemplate = "/getstudyhotspotmembers",
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare)]
        List<StudyHotspotMemberItem> GetStudyHotspotMembers(StudyHotspotGetMemberItem data);
    }
}
